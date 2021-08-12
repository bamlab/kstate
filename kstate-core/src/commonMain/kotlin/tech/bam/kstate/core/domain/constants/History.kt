package tech.bam.kstate.core.domain.constants

import tech.bam.kstate.core.domain.types.StateId

/**
 * [History] is a special state id that points to
 * the previous state id, or null if there is no
 * history.
 * Use [History] in transitions' target.
 */
object History : StateId<Unit>
