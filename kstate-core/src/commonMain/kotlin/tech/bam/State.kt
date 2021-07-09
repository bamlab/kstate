package tech.bam

import tech.bam.domain.constants.RootStateId
import tech.bam.domain.exception.AlreadyRegisteredStateId
import tech.bam.domain.exception.NoRegisteredStates

open class State(val id: StateId, val type: Type, private val strategy: StrategyType) {
    // Protected API
    var transitions: Set<Transition> = setOf()
        protected set
    protected var states: List<State> = listOf()
    var initial: StateId? = null
        protected set
    var currentStateId: StateId? = null
        protected set

    // Private API
    // TODO: Implement the following
    private val onEntry: () -> Unit = {}
    private val onExit: () -> Unit = {}
    var listeners: List<MachineTransitionListener> = listOf()
        private set

    private fun currentState(): State? = states.find { it.id == currentStateId }


    // Public API
    val stateIds: List<StateId>
        get() = states.map { it.id }

    fun isCompound() = states.isNotEmpty()
    fun findTransitionOn(event: Event): Transition? = transitions.find { it.event == event }
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
                val newState = states.find { it.id == transition.target }
                if (newState != null) {
                    currentState.restart()
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

    private fun handleEventWithChildren(event: Event): Boolean {
        return when (type) {
            Type.Hierarchical -> currentState()!!.send(event)
            Type.Parallel -> states.map { it.send(event) }.any { it }
        }
    }

    fun send(event: Event): Boolean {
        val previousActiveStateIds = activeStateIds()
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
            val nextActiveStateIds = activeStateIds()
            notify(previousActiveStateIds, nextActiveStateIds)
        }
        return result
    }

    private fun notify(prev: List<StateId>, next: List<StateId>) {
        listeners.forEach { it.callback(prev, next) }
    }

    fun subscribe(listener: MachineTransitionListener) {
        listeners = listeners.toMutableList().also { it.add(listener) }
    }

    fun unsubscribe(listener: MachineTransitionListener) {
        listeners = listeners.toMutableList().also { it.remove(listener) }
    }

    fun onTransition(callback: (previousActiveStateIds: List<StateId>, nextActiveStateIds: List<StateId>) -> Unit) {
        val newListener = MachineTransitionListener(callback)
        subscribe(newListener)
    }
}

class StateBuilder(
    id: StateId,
    type: Type,
    private val strategy: StrategyType
) : State(id, type, strategy) {
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
        effect: (() -> Unit) = {}
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
        init: StateBuilder.() -> Unit = {}
    ) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createState(id, type, strategy, init)
        states = states.toMutableList().also { it.add(newState) }
    }

    /**
     * Check state machine declaration. You should *NOT* call this function yourself.
     */
    fun build() {
        if (id == RootStateId && states.isEmpty()) throw NoRegisteredStates()
        if (initial == null && states.isNotEmpty())
            initial = states[0].id
        currentStateId = initial
    }
}


internal fun createState(
    id: StateId,
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: StateBuilder .() -> Unit
): State {
    val state = StateBuilder(id, type, strategy)
    state.apply(init)
    state.build()
    return state
}