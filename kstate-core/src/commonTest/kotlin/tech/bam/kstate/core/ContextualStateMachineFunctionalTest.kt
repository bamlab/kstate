package tech.bam.kstate.core

import tech.bam.kstate.core.domain.mock.ChooseAmount
import tech.bam.kstate.core.domain.mock.CreditCardInsideTheATMContext
import tech.bam.kstate.core.domain.mock.InsertCreditCard
import tech.bam.kstate.core.domain.mock.Welcome
import kotlin.test.Test
import kotlin.test.assertEquals

class ContextualStateMachineFunctionalTest {
    @Test
    fun `it should transition and setup a context`() {
        val insertToCreditCardInsideTheATMContextFactory =
            { insertCreditCardEvent: InsertCreditCard ->
                object : CreditCardInsideTheATMContext {
                    override val clientId = insertCreditCardEvent.clientId
                }
            }
        val machine = createMachine {
            initial(Welcome)
            state(Welcome) {
                transition(
                    on = InsertCreditCard::class,
                    target = ChooseAmount,
                    effect = insertToCreditCardInsideTheATMContextFactory
                )
            }
            state(ChooseAmount)
        }

        machine.send(InsertCreditCard("Thomas"))

        assertEquals(
            "Thomas",
            (machine.activeStateIdsWithContext()[0].second as CreditCardInsideTheATMContext).clientId
        )
    }
}