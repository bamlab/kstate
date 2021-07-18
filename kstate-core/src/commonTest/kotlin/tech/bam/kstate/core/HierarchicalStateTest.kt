package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId
import tech.bam.kstate.core.domain.exception.UninitializedContext
import tech.bam.kstate.core.domain.mock.TrafficLightContext
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.GREEN
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.RED
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HierarchicalStateTest {
    @Test
    fun `it registers states`() {
        val stateMachine = createHState(initial = RED) {
            state(RED)
            state(GREEN)
        }

        stateMachine.start()

        assertEquals(listOf(RED, GREEN), stateMachine.stateIds)
    }

    @Test
    fun `it registers the initial state`() {
        val stateMachine = createHState(initial = RED) {
            state(RED)
            state(GREEN)
        }

        stateMachine.start()

        assertEquals((RED), stateMachine.initialStateId)
    }


    @Test
    fun `it rejects already registered states`() {
        assertFailsWith<AlreadyRegisteredStateId> {
            createHState(initial = RED) {
                state(RED)
                state(RED)
            }
        }
    }

    @Test
    fun `it registers event transitions`() {
        val stateMachine = createHState(initial = RED) {
            state(RED) {
                transition(on = TIMER, target = GREEN)
            }
        }

        stateMachine.start()

        val transition = stateMachine.states[0].transitions.elementAt(0)
        assertEquals(TIMER, transition.event)
        assertEquals(GREEN, transition.target)
    }

    @Test
    fun `it registers a context`() {
        val stateMachine = createHStateWithContext<TrafficLightContext>(initial = RED) {
            context(TrafficLightContext("Paris"))
            state(RED) {
                transition(on = TIMER, target = GREEN)
            }
        }

        stateMachine.start()

        assertEquals("Paris", stateMachine.context!!.position)
    }

    @Test
    fun `it throws if no context is passed`() {
        val stateMachine = createHStateWithContext<TrafficLightContext>(initial = RED) {
            state(RED) {
                transition(on = TIMER, target = GREEN)
            }
        }

        assertFailsWith<UninitializedContext> { stateMachine.start() }
    }

    @Test
    fun `it registers on entry callback`() {
        val onEntryCb = mockk<() -> Unit>()
        every { onEntryCb() } returns Unit

        val stateMachine = createHState(initial = RED) {
            onEntry(onEntryCb)

            state(RED) {
                transition(on = TIMER, target = GREEN)
            }
        }

        stateMachine.start()

        verify { onEntryCb() }
        confirmVerified()
    }

    @Test
    fun `it registers on exit callback`() {
        val onExitCb = mockk<() -> Unit>()
        every { onExitCb() } returns Unit

        val stateMachine = createHState(initial = RED) {
            onExit(onExitCb)

            state(RED) {
                transition(on = TIMER, target = GREEN)
            }
        }

        stateMachine.start()
        stateMachine.stop()

        verify { onExitCb() }
        confirmVerified()
    }
}