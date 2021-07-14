package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.Event
import tech.bam.kstate.core.StateId

interface CreditCardInsideTheATMContext {
    val clientId: String
}

class InsertCreditCard(val clientId: String) : Event

object Welcome : StateId
object ChooseAmount : StateId
