package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.Context
import tech.bam.kstate.core.Event
import tech.bam.kstate.core.StateId
import tech.bam.kstate.core.StateIdWithContext

interface CreditCardInsideTheATMContext : Context {
    val clientId: String
}

class InsertCreditCard(val clientId: String) : Event

object Welcome : StateId
object ChooseAmount : StateIdWithContext<CreditCardInsideTheATMContext>
