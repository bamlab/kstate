/*
package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId
import tech.bam.kstate.core.domain.exception.NoRegisteredStates
import tech.bam.kstate.core.domain.mock.MyContext
import tech.bam.kstate.core.domain.mock.MyStateId
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MachineUnitTest {
    @Test
    fun `it registers listeners`() {
        val machine = createMachine {
            initial(RED)
            state(RED)
            state(YELLOW)
            state(GREEN)
        }

        val listener = MachineTransitionListener<Nothing> { _, _ ->
            print("I'm listening.")
        }

        machine.subscribe(listener)

        assertEquals(listOf(listener), machine.listeners)
    }

    @Test
    fun `it unsubscribes its listeners`() {
        val machine = createMachine {
            initial(RED)
            state(RED)
            state(YELLOW)
            state(GREEN)
        }

        val listener = MachineTransitionListener<Nothing> { _, _ ->
            print("I'm listening.")
        }

        machine.subscribe(listener)
        machine.unsubscribe(listener)

        assertEquals(listOf(), machine.listeners)
    }

    @Test
    fun `it creates a listener`() {
        val effect =
            mockk<(previousActiveStateIds: List<StateId>, nextActiveStateIds: List<StateId>) -> Unit>()
        every { effect(any(), any()) } returns Unit

        val machine = createMachine {
            initial(RED)
            state(RED)
            state(YELLOW)
            state(GREEN)
        }

        machine.onTransition { prev, next ->
            effect(prev, next)
        }

        (machine.listeners[0] as MachineTransitionListener).callback(listOf(), listOf())

        verify { effect(listOf(), listOf()) }
        confirmVerified(effect)
    }
}*/
