package tech.bam

sealed class KSTransitionType {
    object External : KSTransitionType()
    object Internal : KSTransitionType()
}

open class KSTransition {
    // Protected API
    var event: KSEvent? = null
        protected set
    var target: KSStateId? = null
        protected set
    var effect: () -> Unit = {}
        protected set

    // TODO : Implement those
    private val condition: Boolean = true
    private val type: KSTransitionType = KSTransitionType.External
}

internal class KSTransitionBuilder : KSTransition() {
    fun setEvent(event: KSEvent?): KSTransitionBuilder {
        this.event = event
        return this
    }

    fun setTarget(target: KSStateId?): KSTransitionBuilder {
        this.target = target
        return this
    }

    fun setEffect(effect: (() -> Unit)): KSTransitionBuilder {
        this.effect = effect
        return this
    }
}

internal fun createTransition(
    on: KSEvent? = null,
    target: KSStateId? = null,
    effect: (() -> Unit) = {}
): KSTransition {
    val ksTransition = KSTransitionBuilder()
    ksTransition
        .setEvent(on)
        .setTarget(target)
        .setEffect(effect)

    return ksTransition
}