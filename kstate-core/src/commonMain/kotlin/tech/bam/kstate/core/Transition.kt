package tech.bam.kstate.core

import kotlin.reflect.KClass

sealed class TransitionType {
    object External : TransitionType()
    object Internal : TransitionType()
}

/**
 * A transition.
 *
 * @param TT the target state type.
 * @param TC the target state context.
 * @param C the current context.
 * @param E the event.
 */
open class Transition<TT, TC, C, E : Any> {
    // Protected API
    var event: E? = null
        protected set
    var eventClass: KClass<E>? = null
        protected set
    var target: StateId<TT, TC>? = null
        protected set
    var effect: ((event: E, context: C) -> TC)? = null
        protected set

    // TODO
    private fun cond(context: C): Boolean = true

    // TODO
    private val type: TransitionType = TransitionType.Internal
}


internal class TransitionBuilder<TT, TC, C, E : Any> : Transition<TT, TC, C, E>() {
    fun setEvent(event: E?): TransitionBuilder<TT, TC, C, E> {
        this.event = event
        return this
    }

    fun setTarget(target: StateId<TT, TC>?): TransitionBuilder<TT, TC, C, E> {
        this.target = target
        return this
    }

    fun setEffect(effect: ((event: E, context: C) -> TC)?): TransitionBuilder<TT, TC, C, E> {
        this.effect = effect
        return this
    }

    fun setEventClass(on: KClass<E>): TransitionBuilder<TT, TC, C, E> {
        this.eventClass = on
        return this
    }
}

internal fun <TT, TC, C, E : Any> createTransition(
    on: E,
    target: StateId<TT, TC>,
    effect: ((event: E, context: C) -> TC)?
): Transition<TT, TC, C, E> {
    val transition = TransitionBuilder<TT, TC, C, E>()
    transition
        .setEvent(on)
        .setTarget(target)
        .setEffect(effect)
    return transition
}