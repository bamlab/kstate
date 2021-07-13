package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
            (machine.activeStateIdsWithContext()[0].context as CreditCardInsideTheATMContext).clientId
        )
    }

    @Test
    fun `it calls listeners with previous and next active state ids`() {
        val effect =
            mockk<(p: List<StateIdWithContextPair<*>>, n: List<StateIdWithContextPair<*>>) -> Unit>()
        every { effect(any(), any()) } returns Unit

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

        machine.onTransitionWithContext(effect)

        machine.send(InsertCreditCard("Thomas"))

        verify {
            effect(
                any(),
                any()
            )
        }
        confirmVerified(effect)
    }
}