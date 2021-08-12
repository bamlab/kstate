package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import tech.bam.kstate.core.domain.mock.CoffeeMachineContext
import tech.bam.kstate.core.domain.mock.RootCoffeeMachineStateId
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.GREEN
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.RED
import tech.bam.kstate.core.domain.types.StateId
import kotlin.test.Test
import kotlin.test.assertEquals

fun <C, PC> createStateWithContext(
    id: StateId<C>,
    context: C,
    init: StateBuilder<C, PC>.() -> Unit
) =
    StateBuilder<C, PC>(id, context).apply(init).build()

fun createState(id: StateId<Any>, init: StateBuilder<Any, Any>.() -> Unit) =
    StateBuilder<Any, Any>(id, Unit).apply(init).build()

class StateTest {
    @Test
    fun `it registers an id`() {
        val stateMachine = createState(id = RED) {}

        stateMachine.start()

        assertEquals(RED, stateMachine.id)
    }

    @Test
    fun `it registers event transitions`() {
        val stateMachine = createState(id = RED) {
            transition(on = TIMER, target = GREEN)
        }

        stateMachine.start()

        val transition = stateMachine.transitions.elementAt(0)
        assertEquals(TIMER, transition.event)
        assertEquals(GREEN, transition.target)
    }

    @Test
    fun `it registers a context`() {
        val stateMachine =
            createStateWithContext<CoffeeMachineContext, Any>(
                id = RootCoffeeMachineStateId,
                context = CoffeeMachineContext(10, 20)
            ) {}

        stateMachine.start()

        assertEquals(20, stateMachine.context.waterStock)
    }

    @Test
    fun `it registers on entry callback`() {
        val onEntryCb = mockk<() -> Unit>()
        every { onEntryCb() } returns Unit

        val stateMachine = createState(id = RED) {
            onEntry(onEntryCb)
        }

        stateMachine.start()

        verify { onEntryCb() }
        confirmVerified()
    }

    @Test
    fun `it registers on exit callback`() {
        val onExitCb = mockk<() -> Unit>()
        every { onExitCb() } returns Unit

        val stateMachine = createState(id = RED) {
            onExit(onExitCb)
        }

        stateMachine.start()
        stateMachine.stop()

        verify { onExitCb() }
        confirmVerified()
    }
}