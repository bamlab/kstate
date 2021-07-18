package tech.bam.kstate.core

/**
 * A State.
 *
 * @param T the concrete representation of this state.
 * @param C the context of this state.
 * @param PC the parent state's context.
 */
interface State<T, C, PC> {
    /**
     * The id of this state.
     * The state id is only an immutable identifier.
     * This state represents [T], which is the concrete
     * and contextual representation of this state.
     */
    val id: StateId<T, C>

    /**
     * The id of the previous state.
     * Can be [null] if this state is the initial state
     * or if this state is a first destination of its parent.
     */
    var history: StateId<T, *>?

    /**
     * The set of transitions allowed by this state.
     */
    var transitions: Set<Transition<T, *, PC, *>>

    /**
     * Call this function to find a matching transition for the given event.
     *
     * @param E a type that is an [Event].
     * @param event the event sent to the state.
     * @return a transition or [null] if nothing is found.
     */
    fun <E : Event> findTransitionOn(event: E): Transition<T, *, PC, E>?

    /**
     * True if this states holds children states.
     */
    val isCompound: Boolean

    /**
     * This state stop logic.
     */
    fun stop()

    /**
     * This state start logic.
     */
    fun start()
}