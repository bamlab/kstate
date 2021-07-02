package tech.bam

class KSState(val id: KSStateId) {
    private val onEntry: () -> Unit = {}
    private val onExit: () -> Unit = {}
    private val transitions: List<KSTransition> = listOf()
    private val states: List<KSState> = listOf()
    private val parallels: List<KSParallel> = listOf()
    private val initial: KSStateId? = null
}