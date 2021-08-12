package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import tech.bam.kstate.core.domain.constants.RootStateId
import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId
import tech.bam.kstate.core.domain.mock.CoffeeMachineContext
import tech.bam.kstate.core.domain.mock.CoffeeMachineStateId
import tech.bam.kstate.core.domain.mock.PedestrianLightStateId.PEDESTRIAN_LIGHT
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.RED
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun createPState(
    init: ParallelStateBuilder<Any, Any>.() -> Unit
) =
    ParallelStateBuilder<Any, Any>(
        id = RootStateId(),
        context = Unit
    ).apply(init).build()

fun <C> createPStateWithContext(
    context: C,
    init: ParallelStateBuilder<C, Any>.() -> Unit
) =
    ParallelStateBuilder<C, Any>(
        id = RootStateId(),
        context = context
    ).apply(init).build()

class ParallelStateTest {
    @Test
    fun `it registers states`() {
        val stateMachine = createPState {
            state(RED)
            state(PEDESTRIAN_LIGHT)
        }

        stateMachine.start()

        assertEquals(listOf(RED, PEDESTRIAN_LIGHT), stateMachine.stateIds)
    }

    @Test
    fun `it rejects already registered states`() {
        assertFailsWith<AlreadyRegisteredStateId> {
            createPState {
                state(RED)
                state(RED)
            }
        }
    }

    @Test
    fun `it registers on entry callback`() {
        val onEntryCb = mockk<() -> Unit>()
        every { onEntryCb() } returns Unit

        val stateMachine = createPState {
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

        val stateMachine = createPState {
            onExit(onExitCb)
        }

        stateMachine.start()
        stateMachine.stop()

        verify { onExitCb() }
        confirmVerified()
    }

    @Test
    fun `it registers a context`() {
        val stateMachine = createPStateWithContext(
            context = CoffeeMachineContext(0, 10)
        ) {
            state(CoffeeMachineStateId.OFF)
        }

        stateMachine.start()

        assertEquals(0, stateMachine.context.coffeeStock)
        assertEquals(10, stateMachine.context.waterStock)
    }
}
