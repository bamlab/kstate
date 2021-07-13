package tech.bam.kstate.core

import tech.bam.kstate.core.domain.constants.RootStateId
import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId
import tech.bam.kstate.core.domain.exception.NoRegisteredStates
import tech.bam.kstate.core.domain.exception.UninitializedContext
import kotlin.reflect.KClass

open class State<C : Context>(
    val id: StateIdWithContext<C>,
    val type: Type,
    private val strategy: StrategyType
) {
    // Protected API
    var transitions: Set<Transition<out Context, out Event>> = setOf()
        protected set
    protected var states: List<State<out Context>> = listOf()
    var initial: StateIdWithContext<out Context>? = null
        protected set
    var currentStateId: StateIdWithContext<out Context>? = null
        protected set
    lateinit var context: C
        protected set

    protected fun isContextInitialized() = this::context.isInitialized

    // Private API
    // TODO: Implement the following
    private val onEntry: () -> Unit = {}
    private val onExit: () -> Unit = {}
    var listeners: List<TransitionListener> = listOf()
        private set

    private fun currentState(): State<out Context>? =
        states.find { it.id == currentStateId }

    private fun findStateWithId(stateId: StateIdWithContext<out Context>): State<out Context>? =
        states.find { it.id == stateId }


    // Public API
    val stateIds: List<StateIdWithContext<out Context>>
        get() = states.map { it.id }

    fun isCompound() = states.isNotEmpty()
    fun <E : Event> findTransitionOn(event: E): Transition<out Context, E>? {
        val transition = transitions.find { it.event == event || it.eventClass == event::class }
        return if (transition == null) null else
            @Suppress("UNCHECKED_CAST")
            transition as Transition<out Context, E>
    }

    fun activeStateIds(): List<StateIdWithContext<out Context>> {
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
                val newState = states.find { it.id == transition.target }
                if (newState != null) {
                    currentState.restart()
                    newState.start(transition.effect(event))
                    currentStateId = newState.id
                    return true
                }
            }
        }
        return false
    }

    private fun restart() {
        if (isCompound()) {
            currentStateId = initial
        }
    }

    fun start(context: Context? = null) {
        if (context != null) {
            @Suppress("UNCHECKED_CAST")
            this.context = context as C
        }
        if (!this.isContextInitialized()) {
            if (id is StateId) {
                // When `id` is of type StateId, this means that C is Context.
                @Suppress("UNCHECKED_CAST")
                this.context = (context ?: object : Context {}) as C
            } else {
                throw UninitializedContext(id)
            }
        }
        currentState()?.start()
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
        prev: List<StateIdWithContextPair<*>>,
        next: List<StateIdWithContextPair<*>>
    ) {
        listeners.forEach {
            when (it) {
                is MachineTransitionListener -> {
                    it.callback(
                        prev.map { p -> p.stateIdWithContext },
                        next.map { p -> p.stateIdWithContext })
                }
                is MachineTransitionWithContextListener -> it.callback(prev, next)
            }
        }
    }

    fun subscribe(listener: TransitionListener) {
        listeners = listeners.toMutableList().also { it.add(listener) }
    }

    fun unsubscribe(listener: TransitionListener) {
        listeners = listeners.toMutableList().also { it.remove(listener) }
    }

    fun onTransition(callback: (previousActiveStateIds: List<StateIdWithContext<out Context>>, nextActiveStateIds: List<StateIdWithContext<out Context>>) -> Unit) {
        val newListener = MachineTransitionListener(callback)
        subscribe(newListener)
    }

    fun onTransitionWithContext(callback: (prev: List<StateIdWithContextPair<*>>, next: List<StateIdWithContextPair<*>>) -> Unit) {
        val newListener = MachineTransitionWithContextListener(callback)
        subscribe(newListener)
    }

    fun activeStateIdsWithContext(): List<StateIdWithContextPair<*>> {
        return if (isCompound()) {
            when (type) {
                Type.Hierarchical ->
                    listOf(
                        @Suppress("UNCHECKED_CAST")
                        listOf(
                            StateIdWithContextPair(
                                currentStateId!! as StateIdWithContext<Context>,
                                currentState()!!.context
                            )
                        ),
                        currentState()!!.activeStateIdsWithContext()
                    ).flatten()
                Type.Parallel -> states.map {
                    listOf(
                        @Suppress("UNCHECKED_CAST")
                        StateIdWithContextPair(
                            it.id as StateIdWithContext<Context>,
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

class StateBuilder<C : Context>(
    id: StateIdWithContext<C>,
    type: Type,
    private val strategy: StrategyType
) : State<C>(id, type, strategy) {
    /**
     * Sets the initial state.
     * This it not used when type is [Type.Parallel].
     *
     * @param id the initial state id.
     */
    fun initial(id: StateIdWithContext<out Context>) {
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
        effect: (() -> Unit) = {}
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
    fun <C : Context> transition(
        on: Event? = null,
        target: StateIdWithContext<C>,
        effect: (() -> C)
    ) {
        val newTransition = createTransition(on, target, effect)
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun <C : Context, E : Event> transition(
        on: KClass<E>,
        target: StateIdWithContext<C>,
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
    fun <C : Context> state(
        id: StateIdWithContext<C>,
        type: Type = Type.Hierarchical,
        strategy: StrategyType = this.strategy,
        init: StateBuilder<C>.() -> Unit = {}
    ) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createState(id, type, strategy, init)
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
     * Check state machine declaration. You should *NOT* call this function yourself.
     */
    fun build() {
        if (id is RootStateId && states.isEmpty()) throw NoRegisteredStates()
        if (initial == null && states.isNotEmpty())
            initial = states[0].id
        currentStateId = initial
    }
}

internal fun <C : Context> createState(
    id: StateIdWithContext<C>,
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: StateBuilder<C>.() -> Unit
): State<C> {
    val state = StateBuilder(id, type, strategy)
    state.apply(init)
    state.build()
    return state
}