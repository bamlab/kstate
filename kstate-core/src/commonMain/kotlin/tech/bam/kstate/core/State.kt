package tech.bam.kstate.core

import tech.bam.kstate.core.domain.exception.UninitializedContext

/**
 * A State.
 *
 * @param T the concrete representation of this state.
 * @param C the context of this state.
 * @param PC the parent state's context.
 */
open class State<T, C, PC>(
    /**
     * The id of this state.
     * The state id is only an immutable identifier.
     * This state represents [T], which is the concrete
     * and contextual representation of this state.
     */
    val id: StateId<T, C>
) {
    /**
     * The context of the state.
     */
    var context: C? = null

    /**
     * The id of the previous state.
     * Can be [null] if this state is the initial state
     * or if this state is a first destination of its parent.
     */
    var history: StateId<T, *>? = null

    /**
     * The set of transitions allowed by this state.
     */
    var transitions: Set<Transition<T, *, PC, *>> = setOf()

    /**
     * The callback fired when this state is active.
     */
    var onEntry: (() -> Unit)? = null

    /**
     * The callback fired when this state becomes inactive.
     */
    var onExit: (() -> Unit)? = null

    /**
     * Call this function to find a matching transition for the given event.
     *
     * @param event the event sent to the state.
     * @return a transition or [null] if nothing is found.
     */
    fun findTransitionOn(event: Any): Transition<T, *, PC, *>? {
        return transitions.find { it.event == event || it.eventClass == event::class }
    }

    /**
     * This state stop logic.
     */
    fun stop() {
        onExit?.let { it() }
    }

    /**
     * This state start logic.
     */
    fun start() {
        if (context == null) {
            throw UninitializedContext(id)
        }
        onEntry?.let { it() }
    }
}

class StateBuilder<T, C, PC>(id: StateId<T, C>) : State<T, C, PC>(id) {
    fun <E : Any, TC> transition(
        on: E,
        target: StateId<T, TC>,
        effect: ((event: E, context: PC) -> TC)? = null
    ) {
        val newTransition = createTransition(on, target, effect)
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun build(): State<T, C, PC> = this
}

fun <T, C, PC> createState(id: StateId<T, C>, init: StateBuilder<T, C, PC>.() -> Unit) =
    StateBuilder<T, C, PC>(id).apply(init).build()