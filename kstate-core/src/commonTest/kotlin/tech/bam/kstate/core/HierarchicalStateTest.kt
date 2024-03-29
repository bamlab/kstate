package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import tech.bam.kstate.core.domain.constants.RootStateId
import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId
import tech.bam.kstate.core.domain.exception.StateNotFound
import tech.bam.kstate.core.domain.mock.CoffeeMachineContext
import tech.bam.kstate.core.domain.mock.CoffeeMachineStateId
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.GREEN
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.RED
import tech.bam.kstate.core.domain.types.StateId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun createHState(
    initial: StateId<*>,
    init: HierarchicalStateBuilder<Any, Any>.() -> Unit
) =
    HierarchicalStateBuilder<Any, Any>(
        id = RootStateId(),
        initialStateId = initial,
        context = Unit
    ).apply(init).build()

fun <C> createHStateWithContext(
    initial: StateId<*>,
    context: C,
    init: HierarchicalStateBuilder<C, Any>.() -> Unit
) =
    HierarchicalStateBuilder<C, Any>(
        id = RootStateId(),
        initialStateId = initial,
        context = context
    ).apply(init).build()

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
    fun `it fails when initial state is not declared`() {
        assertFailsWith<StateNotFound> {
            val stateMachine = createHState(initial = RED) {
            }

            stateMachine.start()
        }
    }


    @Test
    fun `it registers on entry callback`() {
        val onEntryCb = mockk<() -> Unit>()
        every { onEntryCb() } returns Unit

        val stateMachine = createHState(initial = RED) {
            onEntry(onEntryCb)
            state(RED)
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
            state(RED)
        }

        stateMachine.start()
        stateMachine.stop()

        verify { onExitCb() }
        confirmVerified()
    }

    @Test
    fun `it registers a context`() {
        val stateMachine = createHStateWithContext(
            initial = CoffeeMachineStateId.OFF,
            context = CoffeeMachineContext(0, 10)
        ) {
            state(CoffeeMachineStateId.OFF)
        }

        stateMachine.start()

        assertEquals(0, stateMachine.context.coffeeStock)
        assertEquals(10, stateMachine.context.waterStock)
    }
}
