package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId
import tech.bam.domain.exception.NoRegisteredStates

open class KSState(val id: KSStateId, private val strategy: KSStrategyType) {
    // Protected API
    var transitions: Set<KSTransition> = setOf()
        protected set
    protected var states: List<KSState> = listOf()
    var initial: KSStateId? = null
        protected set
    var currentStateId: KSStateId? = null
        protected set

    // Private API
    // TODO: Implement the following
    private val onEntry: () -> Unit = {}
    private val onExit: () -> Unit = {}
    protected open fun currentState(): KSState? = states.find { it.id == currentStateId }

    // Public API
    val stateIds: List<KSStateId>
        get() = states.map { it.id }

    fun isCompound() = states.isNotEmpty()
    fun findTransitionOn(event: KSEvent): KSTransition? = transitions.find { it.event == event }
    fun activeStateIds(): List<KSStateId> {
        if (isCompound()) return listOf(
            listOf(currentStateId!!),
            currentState()!!.activeStateIds()
        ).flatten()
        return listOf()
    }

    private fun handleEventWithTransition(event: KSEvent): Boolean {
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

    private fun handleEventWithChildren(event: KSEvent) = currentState()!!.send(event)

    fun send(event: KSEvent): Boolean = if (isCompound()) {
        when (strategy) {
            KSStrategyType.External -> {
                handleEventWithTransition(event) || handleEventWithChildren(event)
            }
            KSStrategyType.Internal -> {
                handleEventWithChildren(event) || handleEventWithTransition(event)
            }
        }
    } else {
        false
    }
}

class KSStateBuilder(
    id: KSStateId,
    private val strategy: KSStrategyType
) : KSState(id, strategy) {
    fun initial(id: KSStateId) {
        initial = id
    }

    fun transition(
        on: KSEvent? = null,
        target: KSStateId? = null,
        effect: (() -> Unit) = {}
    ) {
        val newTransition = createTransition(on, target, effect)
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun state(
        id: KSStateId,
        strategy: KSStrategyType = this.strategy,
        init: KSStateBuilder.() -> Unit = {}
    ) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createState(id, strategy, init)
        states = states.toMutableList().also { it.add(newState) }
    }

    fun build() {
        if (id == KSRoot && states.isEmpty()) throw NoRegisteredStates()
        if (initial == null && states.isNotEmpty())
            initial = states[0].id
        currentStateId = initial
    }
}


internal fun createState(
    id: KSStateId,
    strategy: KSStrategyType = KSStrategyType.External,
    init: KSStateBuilder.() -> Unit
): KSState {
    val ksState = KSStateBuilder(id, strategy)
    ksState.apply(init)

    ksState.build()
    return ksState
}