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
    var event: Event? = null
        protected set
    var eventClass: KClass<E>? = null
        protected set
    var target: StateId<TT, TC>? = null
        protected set
    lateinit var effect: (event: E) -> TC?
        protected set

    // TODO
    private fun cond(context: C): Boolean = true

    // TODO
    private val type: TransitionType = TransitionType.Internal
}
/*

internal class TransitionBuilder<C : Any, E : Event> : Transition<C, E>() {
    fun setEvent(event: Event?): TransitionBuilder<C, E> {
        this.event = event
        return this
    }

    fun setTarget(target: StateId?): TransitionBuilder<C, E> {
        this.target = target
        return this
    }

    fun setEffect(effect: (() -> C?)): TransitionBuilder<C, E> {
        this.effect = { _ -> effect() }
        return this
    }

    fun setEventEffect(effect: ((event: E) -> C)): TransitionBuilder<C, E> {
        this.effect = effect
        return this
    }

    fun setEventClass(on: KClass<E>): TransitionBuilder<C, E> {
        this.eventClass = on
        return this
    }
}

internal fun <C : Any> createTransition(
    on: Event? = null,
    target: StateId? = null,
    effect: (() -> C?) = { null }
): Transition<C, Event> {
    val transition = TransitionBuilder<C, Event>()
    transition
        .setEvent(on)
        .setTarget(target)
        .setEffect {
            effect()
        }

    return transition
}

internal fun <C : Any, E : Event> createTransition(
    on: KClass<E>,
    target: StateId,
    effect: ((event: E) -> C)
): Transition<C, E> {
    val transition = TransitionBuilder<C, E>()
    transition
        .setEventClass(on)
        .setTarget(target)
        .setEventEffect(effect)

    return transition
}*/
