package tech.bam.kstate.core

import tech.bam.kstate.core.domain.constants.History
import tech.bam.kstate.core.domain.constants.RootStateId
import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId
import tech.bam.kstate.core.domain.exception.NoRegisteredStates
import kotlin.reflect.KClass

open class DeprecatedState<C : Any>(
    val id: StateId,
    val type: Type,
    private val strategy: StrategyType
) {
    var history: StateId? = null
    var historyContext: C? = null

    // Protected API
    var transitions: Set<Transition<C, out Event>> = setOf()
        protected set
    protected var states: List<DeprecatedState<C>> = listOf()
    var initial: StateId? = null
        protected set
    var currentStateId: StateId? = null
        protected set
    var context: C? = null
        protected set

    // Private API
    // TODO: Implement the following
    var onEntry: (() -> Unit)? = null
        protected set
    private val onExit: () -> Unit = {}
    var listeners: List<TransitionListener<C>> = listOf()
        private set

    private fun currentState(): DeprecatedState<C>? =
        states.find { it.id == currentStateId }

    private fun findStateWithId(stateId: StateId): DeprecatedState<C>? =
        states.find { it.id == stateId }


    // Public API
    val stateIds: List<StateId>
        get() = states.map { it.id }

    fun isCompound() = states.isNotEmpty()
    fun <E : Event> findTransitionOn(event: E): Transition<C, E>? {
        val transition = transitions.find { it.event == event || it.eventClass == event::class }
        return if (transition == null) null else
            @Suppress("UNCHECKED_CAST")
            transition as Transition<C, E>
    }

    fun activeStateIds(): List<StateId> {
        return if (isCompound()) {
            when (type) {
                Type.Hierarchical ->
                    listOf(
                        listOf(currentStateId!!),
                        currentState()!!.activeStateIds()
                    ).flatten()
                Type.Parallel -> states.map { listOf(it.id, *it.activeStateIds().toTypedArray()) }
                    .flatten()
            }
        } else {
            listOf()
        }
    }

    private fun handleEventWithTransition(event: Event): Boolean {
        val currentState = currentState()!!
        val transition = currentState.findTransitionOn(event)
        if (transition != null) {
            if (transition.target != null) {
                if (transition.target == History) {
                    if (history != null) {
                        currentState.stop()
                        currentStateId = history
                        currentState()?.start(historyContext)
                        history = null
                        historyContext = null
                        return true
                    }
                } else {
                    val newState = states.find { it.id == transition.target }
                    if (newState != null) {
                        history = currentStateId
                        historyContext = currentState.context
                        currentState.stop()
                        newState.start(transition.effect(event))
                        currentStateId = newState.id
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun stop() {
        context = null
        if (isCompound()) {
            currentStateId = initial
        }
    }

    fun start(context: C? = null) {
        if (context != null) {
            this.context = context
        }
        onEntry?.let { it() }
        when (type) {
            Type.Hierarchical -> currentState()?.start()
            Type.Parallel -> states.forEach { it.start() }
        }
    }

    private fun handleEventWithChildren(event: Event): Boolean {
        return when (type) {
            Type.Hierarchical -> currentState()!!.send(event)
            Type.Parallel -> states.map { it.send(event) }.any { it }
        }
    }

    fun send(event: Event): Boolean {
        val previousActiveStateWithContextIds = activeStateIdsWithContext()
        val result = if (isCompound()) {
            when (strategy) {
                StrategyType.External -> {
                    handleEventWithTransition(event) || handleEventWithChildren(event)
                }
                StrategyType.Internal -> {
                    handleEventWithChildren(event) || handleEventWithTransition(event)
                }
            }
        } else {
            false
        }
        if (result) {
            val nextActiveStateWithContextIds = activeStateIdsWithContext()
            notify(previousActiveStateWithContextIds, nextActiveStateWithContextIds)
        }
        return result
    }

    private fun notify(
        prev: List<StateIdWithContextPair<C>>,
        next: List<StateIdWithContextPair<C>>
    ) {
        listeners.forEach {
            when (it) {
                is MachineTransitionListener -> {
                    it.callback(
                        prev.map { p -> p.stateIdWithContext },
                        next.map { p -> p.stateIdWithContext })
                }
                is MachineTransitionWithContextListener<C> -> it.callback(prev, next)
            }
        }
    }

    fun subscribe(listener: TransitionListener<C>) {
        listeners = listeners.toMutableList().also { it.add(listener) }
    }

    fun unsubscribe(listener: TransitionListener<C>) {
        listeners = listeners.toMutableList().also { it.remove(listener) }
    }

    fun onTransition(callback: (previousActiveStateIds: List<StateId>, nextActiveStateIds: List<StateId>) -> Unit) {
        val newListener = MachineTransitionListener<C>(callback)
        subscribe(newListener)
    }

    fun onTransitionWithContext(callback: (prev: List<StateIdWithContextPair<C>>, next: List<StateIdWithContextPair<C>>) -> Unit) {
        val newListener = MachineTransitionWithContextListener(callback)
        subscribe(newListener)
    }

    fun activeStateIdsWithContext(): List<StateIdWithContextPair<C>> {
        return if (isCompound()) {
            when (type) {
                Type.Hierarchical ->
                    listOf(
                        @Suppress("UNCHECKED_CAST")
                        listOf(
                            StateIdWithContextPair(
                                currentStateId!!,
                                currentState()!!.context
                            )
                        ),
                        currentState()!!.activeStateIdsWithContext()
                    ).flatten()
                Type.Parallel -> states.map {
                    listOf(
                        StateIdWithContextPair(
                            it.id,
                            findStateWithId(it.id)!!.context
                        ),
                        *it.activeStateIdsWithContext().toTypedArray()
                    )
                }
                    .flatten()
            }
        } else {
            listOf()
        }
    }
}

class DeprecatedStateBuilder<C : Any>(
    id: StateId,
    type: Type,
    val strategy: StrategyType
) : DeprecatedState<C>(id, type, strategy) {
    /**
     * Sets the initial state.
     * This it not used when type is [Type.Parallel].
     *
     * @param id the initial state id.
     */
    fun initial(id: StateId) {
        initial = id
    }

    /**
     * Declare a transition.
     *
     * @param on the event the state should react to.
     * @param target the target [StateId].
     * @param effect a side effect called when transition is used.
     */
    fun transition(
        on: Event? = null,
        target: StateId? = null,
        effect: (() -> C?) = { null }
    ) {
        val newTransition = createTransition(on, target, effect)
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    /**
     * Declare a transition.
     *
     * @param on the event the state should react to.
     * @param target the target [StateId].
     * @param effect a side effect called when transition is used. It should return a context for the given target.
     */
    fun <E : Event> transition(
        on: KClass<E>,
        target: StateId,
        effect: ((event: E) -> C)
    ) {
        val newTransition = createTransition(on, target, effect)
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    /**
     * Declare a child transition.
     * Doing so, the current state becomes a compound state.
     *
     * @param id the [StateId] of the child.
     * @param type the type of the state machine. Either [Type.Hierarchical] or [Type.Parallel].
     * @param strategy the strategy of the state machine.
     *  Either [StrategyType.External] or [StrategyType.Internal].
     *  *kstate* introduces [StrategyType] concept. Default to [StrategyType.External].
     *  When set to [StrategyType.Internal], events are handled by children first, and
     *  then the compound state.
     * @param init use *kstate*'s DSL to declare your state machine.
     */
    fun state(
        id: StateId,
        type: Type = Type.Hierarchical,
        strategy: StrategyType = this.strategy,
        init: DeprecatedStateBuilder<C>.() -> Unit = {}
    ) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createContextState(id, type, strategy, init)
        states = states.toMutableList().also { it.add(newState) }
    }

    /**
     * Declares a context.
     *
     * @param context the context to be registered in the current state.
     */
    fun context(context: C) {
        this.context = context
    }

    /**
     * Declares a callback that runs on state entry.
     *
     * @param callback
     */
    fun onEntry(callback: () -> Unit) {
        onEntry = callback
    }

    /**
     * Check state machine declaration. You should *NOT* call this function yourself.
     */
    fun build(): DeprecatedState<C> {
        if (id is RootStateId && states.isEmpty()) throw NoRegisteredStates()
        if (initial == null && states.isNotEmpty())
            initial = states[0].id
        currentStateId = initial
        return this
    }
}

internal fun createState(
    id: StateId,
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: DeprecatedStateBuilder<Nothing>.() -> Unit
) = createContextState(id, type, strategy, init)

internal fun <C : Any> createContextState(
    id: StateId,
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: DeprecatedStateBuilder<C>.() -> Unit
) = DeprecatedStateBuilder<C>(id, type, strategy).apply(init).build()
