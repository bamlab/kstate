package tech.bam.kstate.core

import kotlin.reflect.KClass

sealed class TransitionType {
    object External : TransitionType()
    object Internal : TransitionType()
}

open class Transition<C : Context, E : Event> {
    // Protected API
    var event: Event? = null
        protected set
    var eventClass: KClass<E>? = null
        protected set
    var target: StateIdWithContext<C>? = null
        protected set
    lateinit var effect: (event: E) -> C
        protected set

    // TODO : Implement those
    private val condition: Boolean = true
    private val type: TransitionType = TransitionType.External
}

internal class TransitionBuilder<C : Context, E : Event> : Transition<C, E>() {
    fun setEvent(event: Event?): TransitionBuilder<C, E> {
        this.event = event
        return this
    }

    fun setTarget(target: StateIdWithContext<C>?): TransitionBuilder<C, E> {
        this.target = target
        return this
    }

    fun setEffect(effect: (() -> C)): TransitionBuilder<C, E> {
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

internal fun <C : Context> createTransition(
    on: Event? = null,
    target: StateIdWithContext<C>? = null,
    effect: (() -> C)
): Transition<C, Event> {
    val transition = TransitionBuilder<C, Event>()
    transition
        .setEvent(on)
        .setTarget(target)
        .setEffect(effect)

    return transition
}

internal fun createTransition(
    on: Event? = null,
    target: StateId? = null,
    effect: (() -> Unit) = {}
): Transition<Context, Event> {
    val transition = TransitionBuilder<Context, Event>()
    transition
        .setEvent(on)
        .setTarget(target)
        .setEffect {
            effect()
            object : Context {}
        }

    return transition
}

internal fun <C : Context, E : Event> createTransition(
    on: KClass<E>,
    target: StateIdWithContext<C>,
    effect: ((event: E) -> C)
): Transition<C, E> {
    val transition = TransitionBuilder<C, E>()
    transition
        .setEventClass(on)
        .setTarget(target)
        .setEventEffect(effect)

    return transition
}