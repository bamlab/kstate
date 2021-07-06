package tech.bam

sealed class TransitionType {
    object External : TransitionType()
    object Internal : TransitionType()
}

open class Transition {
    // Protected API
    var event: Event? = null
        protected set
    var target: StateId? = null
        protected set
    var effect: () -> Unit = {}
        protected set

    // TODO : Implement those
    private val condition: Boolean = true
    private val type: TransitionType = TransitionType.External
}

internal class TransitionBuilder : Transition() {
    fun setEvent(event: Event?): TransitionBuilder {
        this.event = event
        return this
    }

    fun setTarget(target: StateId?): TransitionBuilder {
        this.target = target
        return this
    }

    fun setEffect(effect: (() -> Unit)): TransitionBuilder {
        this.effect = effect
        return this
    }
}

internal fun createTransition(
    on: Event? = null,
    target: StateId? = null,
    effect: (() -> Unit) = {}
): Transition {
    val transition = TransitionBuilder()
    transition
        .setEvent(on)
        .setTarget(target)
        .setEffect(effect)

    return transition
}