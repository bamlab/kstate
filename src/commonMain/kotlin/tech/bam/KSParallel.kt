package tech.bam

class KSParallel {
    private val id: KSStateId? = null
    private val onEntry: () -> Unit = {}
    private val onExit: () -> Unit = {}
    private val transitions: List<KSTransition> = listOf()
    private val states: List<KSState> = listOf()
    private val parallels: List<KSParallel> = listOf()
}
