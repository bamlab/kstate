package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId

open class KSState(val id: KSStateId) {
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
    private val parallels: List<KSParallel> = listOf()
    private fun currentState(): KSState? = states.find { it.id == currentStateId }

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
}

class KSStateBuilder(id: KSStateId) : KSState(id) {
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

    fun state(id: KSStateId, init: KSStateBuilder.() -> Unit = {}) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createState(id, init)
        states = states.toMutableList().also { it.add(newState) }
    }

    fun build() {
        if (initial == null && states.isNotEmpty())
            initial = states[0].id
        currentStateId = initial
    }
}


internal fun createState(id: KSStateId, init: KSStateBuilder.() -> Unit): KSState {
    val ksState = KSStateBuilder(id)
    ksState.apply(init)

    ksState.build()
    return ksState
}