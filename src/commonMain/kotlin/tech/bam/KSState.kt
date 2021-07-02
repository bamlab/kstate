package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId

open class KSState(val id: KSStateId) {
    // Protected API
    var transitions: Set<KSTransition> = setOf()
        protected set
    protected var states: List<KSState> = listOf()
    var initial: KSStateId? = null
        protected set

    // TODO: Implement the following
    private val onEntry: () -> Unit = {}
    private val onExit: () -> Unit = {}
    private val parallels: List<KSParallel> = listOf()


    // Public API
    val stateIds: List<KSStateId>
        get() = states.map { it.id }
}

internal class KSStateBuilder(id: KSStateId) : KSState(id) {
    fun initial(id: KSStateId) {
        initial = id
    }

    fun transition() {
        val newTransition = KSTransition()
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun state(id: KSStateId) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = KSState(id)
        states = states.toMutableList().also { it.add(newState) }
    }

    fun build() {
        if (initial == null && states.isNotEmpty())
            initial = states[0].id
    }
}


internal fun createState(id: KSStateId, init: KSStateBuilder.() -> Unit): KSState {
    val ksState = KSStateBuilder(id)
    ksState.apply(init)

    ksState.build()
    return ksState
}