package tech.bam.kstate.core

import tech.bam.kstate.core.domain.types.StateId
import tech.bam.kstate.core.domain.types.TransitionType
import kotlin.reflect.KClass

/**
 * A transition.
 *
 * @param TC the target state context.
 * @param C the current context.
 * @param E the event.
 */
open class Transition<TC, C, E : Any>(
    event: E? = null,
    eventClass: KClass<E>? = null,
    target: StateId<TC>? = null,
    effect: ((event: E, context: C) -> TC)? = null,
    val cond: ((context: C) -> Boolean)? = null,
    val isAlways: Boolean = false,
    // TODO : Implement this
    val type: TransitionType = TransitionType.Internal
) {
    var event: E? = null
        private set
    var eventClass: KClass<E>? = null
        private set
    var target: StateId<TC>? = null
        private set
    var effect: ((event: E, context: C) -> TC)? = null
        private set

    init {
        this.event = event
        this.eventClass = eventClass
        this.target = target
        this.effect = effect
    }
}
