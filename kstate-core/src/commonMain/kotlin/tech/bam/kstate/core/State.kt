package tech.bam.kstate.core

import tech.bam.kstate.core.domain.types.StateId
import kotlin.reflect.KClass

/**
 * A State.
 *
 * @param C the context of this state.
 * @param PC the parent state's context.
 */
open class State<C, PC>(
    /**
     * The id of this state.
     * The state id is only an immutable identifier.
     */
    val id: StateId<C>,
    /**
     * The context of the state.
     */
    var context: C
) {
    /**
     * The set of transitions allowed by this state.
     */
    var transitions: Set<Transition<*, PC, *>> = setOf()

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
    fun <E : Any> findTransitionOn(event: E, context: PC): Transition<*, PC, E>? {
        // TODO: Secure this unchecked cast.
        @Suppress("UNCHECKED_CAST")
        return transitions.find {
            (it.event == event || it.eventClass?.isInstance(event) == true)
                    && (it.cond == null || it.cond.invoke(context))

        } as Transition<*, PC, E>?
    }

    /**
     * This state stop logic.
     */
    open fun stop() {
        onExit?.let { it() }
    }

    /**
     * This state start logic.
     */
    open fun start() {
        onEntry?.let { it() }
    }

    /**
     * Lists all active state ids.
     *
     * @return a list of state ids.
     */
    fun activeStateIds(): List<StateId<*>> {
        return listOf(id)
    }

    fun assign(context: C) {
        this.context = context
    }
}

class StateBuilder<C, PC>(id: StateId<C>, context: C) : State<C, PC>(id, context) {
    fun <E : Any, TargetContext> transition(
        on: E,
        target: StateId<TargetContext>,
        effect: ((context: PC) -> TargetContext)? = null
    ) {
        val newTransition = if (effect == null) Transition<TargetContext, PC, E>(
            event = on,
            target = target,
        ) else Transition(
            event = on,
            target = target,
            effect = { _, context -> effect(context) }
        )
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun <E : Any, TargetContext> transition(
        on: KClass<E>,
        target: StateId<TargetContext>,
        effect: ((event: E, context: PC) -> TargetContext)? = null
    ) {
        val newTransition = Transition(
            eventClass = on,
            target = target,
            effect = effect
        )
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun always(
        target: StateId<Any>,
        cond: ((context: PC) -> Boolean)
    ) {
        val newTransition = Transition<Any, PC, Any>(
            target = target,
            cond = cond,
            isAlways = true
        )
        transitions = transitions.toMutableSet().also { it.add(newTransition) }
    }

    fun onEntry(onEntry: () -> Unit) {
        this.onEntry = onEntry
    }

    fun onExit(onExit: () -> Unit) {
        this.onExit = onExit
    }

    fun build(): State<C, PC> = this
}
