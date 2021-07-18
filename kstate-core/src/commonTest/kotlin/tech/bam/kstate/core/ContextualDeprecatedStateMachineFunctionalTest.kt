/*
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

class ContextualDeprecatedStateMachineFunctionalTest {
    @Test
    fun `it should transition and setup a context`() {
        val insertToCreditCardInsideTheATMContextFactory =
            { insertCreditCardEvent: InsertCreditCard ->
                object : CreditCardInsideTheATMContext {
                    override val clientId = insertCreditCardEvent.clientId
                }
            }
        val machine = createContextMachine<CreditCardInsideTheATMContext> {
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

        var createdContext: CreditCardInsideTheATMContext? = null

        val insertToCreditCardInsideTheATMContextFactory =
            { insertCreditCardEvent: InsertCreditCard ->
                createdContext = object : CreditCardInsideTheATMContext {
                    override val clientId = insertCreditCardEvent.clientId
                }
                createdContext as CreditCardInsideTheATMContext
            }
        val machine = createContextMachine<CreditCardInsideTheATMContext> {
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
                listOf(StateIdWithContextPair(Welcome, null)),
                listOf(StateIdWithContextPair(ChooseAmount, createdContext))
            )
        }
        confirmVerified(effect)
    }

    */
/*@Test
    fun `it uses context factory`() {
        val listener =
            mockk<(p: List<StateIdWithContextPair<Int>>, n: List<StateIdWithContextPair<Int>>) -> Unit>()
        every { listener(any(), any()) } returns Unit
        val machine = createContextMachine<Int> {
            initial(RED)
            state(RED) {
                context(10)
                transition(on = TIMER, target = GREEN)
            }
            state(GREEN) {
                transition(on = TIMER, target = RED)
            }
        }

        machine.onTransitionWithContext(listener)

        machine.send(TIMER)
        verify {
            listener(
                listOf(StateIdWithContextPair(RED, 10)),
                listOf(StateIdWithContextPair(GREEN, null))
            )
        }

        machine.send(TIMER)
        verify {
            listener(
                listOf(StateIdWithContextPair(GREEN, null)),
                listOf(StateIdWithContextPair(RED, 10))
            )
        }

        confirmVerified(listener)
    }*//*

}*/
