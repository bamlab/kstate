package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import tech.bam.kstate.core.domain.constants.History
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.*
import kotlin.test.Test
import kotlin.test.assertEquals

object BACK_IN_TIME : Event

class StateHistoryUnitTest {
    @Test
    fun `it registers history`() {
        val machine = createMachine {
            initial(RED)
            state(RED) {
                transition(on = TIMER, target = GREEN)
            }
            state(GREEN) {
                transition(on = TIMER, target = YELLOW)
            }
            state(YELLOW) {
                transition(on = TIMER, target = RED)
            }
        }

        assertEquals(null, machine.history)

        machine.send(TIMER)

        assertEquals(RED, machine.history)
    }

    @Test
    fun `it transitions to history`() {
        val machine = createMachine {
            initial(RED)
            state(RED) {
                transition(on = TIMER, target = GREEN)
            }
            state(GREEN) {
                transition(on = BACK_IN_TIME, target = History)
                transition(on = TIMER, target = YELLOW)
            }
            state(YELLOW) {
                transition(on = TIMER, target = RED)
            }
        }

        assertEquals(null, machine.history)

        machine.send(TIMER)
        machine.send(BACK_IN_TIME)

        assertEquals(null, machine.history)
        assertEquals(RED, machine.currentStateId)
    }

    @Test
    fun `it transitions to history and keep context`() {
        val listener =
            mockk<(p: List<StateIdWithContextPair<Int>>, n: List<StateIdWithContextPair<Int>>) -> Unit>()
        every { listener(any(), any()) } returns Unit
        val machine = createContextMachine<Int> {
            initial(RED)
            state(RED) {
                transition(on = TIMER, target = GREEN) { 10 }
            }
            state(GREEN) {
                transition(on = TIMER, target = YELLOW)
            }
            state(YELLOW) {
                transition(on = BACK_IN_TIME, target = History)
                transition(on = TIMER, target = RED)
            }
        }

        machine.onTransitionWithContext(listener)

        machine.send(TIMER)
        verify {
            listener(
                listOf(StateIdWithContextPair(RED, null)),
                listOf(StateIdWithContextPair(GREEN, 10))
            )
        }

        machine.send(TIMER)
        verify {
            listener(
                listOf(StateIdWithContextPair(GREEN, 10)),
                listOf(StateIdWithContextPair(YELLOW, null))
            )
        }

        machine.send(BACK_IN_TIME)
        verify {
            listener(
                listOf(StateIdWithContextPair(YELLOW, null)),
                listOf(StateIdWithContextPair(GREEN, 10))
            )
        }

        confirmVerified(listener)
    }
}