package tech.bam.kstate.core.domain.types

/**
 * Transition type is either:
 *
 * - [External]. We target a state that is outside of the current compound state.
 * - [Internal]. We target a state that is inside the current compound state.
 *
 * By default, a transition is [Internal].
 */
sealed class TransitionType {
    // TODO: Implement external transition type.
    object External : TransitionType()
    object Internal : TransitionType()
}