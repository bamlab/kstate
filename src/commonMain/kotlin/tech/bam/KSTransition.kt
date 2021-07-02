package tech.bam

sealed class KSTransitionType {
    object External : KSTransitionType()
    object Internal : KSTransitionType()
}

class KSTransition {
    private val event: KSEvent? = null
    private val condition: Boolean = true
    private val target: KSStateId? = null
    private val type: KSTransitionType = KSTransitionType.External
    private val effect: () -> Unit = {}
}