package tech.bam.kstate.core.domain.types

/**
 * A State Identifier.
 *
 * @param Context States can hold data. Context represents the interface of a state's data.
 */
interface StateId<Context>

/**
 * A basic state id type alias that has no context.
 */
typealias SimpleStateId = StateId<Any>
