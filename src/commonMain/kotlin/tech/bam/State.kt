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
    protected open fun currentState(): State? = states.find { it.id == currentStateId }

    // Public API
    val stateIds: List<StateId>
        get() = states.map { it.id }

    fun isCompound() = states.isNotEmpty()
    fun findTransitionOn(event: Event): Transition? = transitions.find { it.event == event }
    fun activeStateIds(): List<StateId> {
        if (isCompound()) return listOf(
            listOf(currentStateId!!),
            currentState()!!.activeStateIds()
        ).flatten()
        return listOf()
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

    private fun handleEventWithChildren(event: Event) = currentState()!!.send(event)

    fun send(event: Event): Boolean = if (isCompound()) {
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
}

class StateBuilder(
    id: StateId,
    type: Type,
    private val strategy: StrategyType
) : State(id, type, strategy) {
    fun initial(id: StateId) {
        initial = id
    }

    fun transition(
        on: Event? = null,
        target: StateId? = null,
        effect: (() -> Unit) = {}
    ) {
        val newTransition = createTransition(on, target, effect)
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun state(
        id: StateId,
        strategy: StrategyType = this.strategy,
        init: StateBuilder.() -> Unit = {}
    ) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createState(id, strategy = strategy, init = init)
        states = states.toMutableList().also { it.add(newState) }
    }

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