package tech.bam.kstate.core.domain.types

/**
 * Defines a state event passing strategy.
 * [External] is the default value.
 *
 * - [External] sends the event to compound states, and
 *   if not handled, pass it to the child state.
 * - [Internal] sends the event to the deepest state, and
 *   if not handled, pass it to the compound state.
 */
sealed class Strategy {
    object External : Strategy()
    object Internal : Strategy()
}