package tech.bam

import tech.bam.TrafficLightStateId.*
import tech.bam.domain.exception.AlreadyRegisteredStateId
import tech.bam.domain.exception.NoRegisteredStates
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

enum class TrafficLightStateId : KSStateId {
    RED, YELLOW, GREEN
}

class KSMachineTest {
    @Test
    fun `it registers states`() {
        val machine = createMachine {
            state(RED)
            state(YELLOW)
            state(GREEN)
        }
        assertEquals(
            machine.stateIds,
            listOf(RED, YELLOW, GREEN)
        )
    }

    @Test
    fun `it rejects already registered states`() {
        assertFailsWith<AlreadyRegisteredStateId> {
            createMachine {
                state(RED)
                state(YELLOW)
                state(RED)
            }
        }
    }

    @Test
    fun `it registers an initial state`() {
        val machine = createMachine {
            initial(RED)
            state(RED)
            state(YELLOW)
            state(GREEN)
        }

        assertEquals(RED, machine.initial)
    }

    @Test
    fun `it uses the first state as initial state when no initial state is declared`() {
        val machine = createMachine {
            state(GREEN)
            state(RED)
            state(YELLOW)
        }

        assertEquals(GREEN, machine.initial)
    }

    @Test
    fun `it fails when no state is passed`() {
        assertFailsWith<NoRegisteredStates> { createMachine {} }
    }
}