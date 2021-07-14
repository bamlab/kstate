package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.StateId

object MyStateId : StateId

interface MyContext {
    val myBoolean: Boolean
}