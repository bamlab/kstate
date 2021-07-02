package tech.bam

open class KSState(val id: KSStateId) {
    // Protected API
    var transitions: Set<KSTransition> = setOf()
        protected set

    // TODO: Implement the following
    private val onEntry: () -> Unit = {}
    private val onExit: () -> Unit = {}
    private val states: List<KSState> = listOf()
    private val parallels: List<KSParallel> = listOf()
    private val initial: KSStateId? = null
}

internal class KSStateBuilder(id: KSStateId) : KSState(id) {
    fun transition() {
        val newTransition = KSTransition()
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun build() {
    }
}


internal fun createState(id: KSStateId, init: KSStateBuilder.() -> Unit): KSState {
    val ksState = KSStateBuilder(id)
    ksState.apply(init)

    ksState.build()
    return ksState
}