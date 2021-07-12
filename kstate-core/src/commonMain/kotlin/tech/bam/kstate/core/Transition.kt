package tech.bam.kstate.core

sealed class TransitionType {
    object External : TransitionType()
    object Internal : TransitionType()
}

open class Transition<C : Context> {
    // Protected API
    var event: Event? = null
        protected set
    var target: StateIdWithContext<C>? = null
        protected set

    // TODO: Implement this.
    var effect: () -> Unit = {}
        protected set

    // TODO : Implement those
    private val condition: Boolean = true
    private val type: TransitionType = TransitionType.External
}

internal class TransitionBuilder<C : Context> : Transition<C>() {
    fun setEvent(event: Event?): TransitionBuilder<C> {
        this.event = event
        return this
    }

    fun setTarget(target: StateIdWithContext<C>?): TransitionBuilder<C> {
        this.target = target
        return this
    }

    fun setEffect(effect: (() -> Unit)): TransitionBuilder<C> {
        this.effect = effect
        return this
    }
}

internal fun <C : Context> createTransition(
    on: Event? = null,
    target: StateIdWithContext<C>? = null,
    effect: (() -> Unit) = {}
): Transition<C> {
    val transition = TransitionBuilder<C>()
    transition
        .setEvent(on)
        .setTarget(target)
        .setEffect(effect)

    return transition
}