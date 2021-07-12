package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.Context
import tech.bam.kstate.core.StateIdWithContext

object MyStateIdWithContext : StateIdWithContext<MyContext>

interface MyContext : Context {
    val myBoolean: Boolean
}