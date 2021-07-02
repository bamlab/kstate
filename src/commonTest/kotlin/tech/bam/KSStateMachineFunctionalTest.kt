package tech.bam

import tech.bam.domain.mockk.PedestrianLightStateId.WAIT
import tech.bam.domain.mockk.PedestrianLightStateId.WALK
import tech.bam.domain.mockk.TrafficLightEvent.SHORT_TIMER
import tech.bam.domain.mockk.TrafficLightEvent.TIMER
import tech.bam.domain.mockk.TrafficLightStateId.*
import kotlin.test.Test
import kotlin.test.assertEquals

class KSStateMachineFunctionalTest {
    @Test
    fun `it transitions between states`() {
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

        machine.send(TIMER)

        assertEquals(GREEN, machine.currentStateId)
    }

    @Test
    fun `it gets active states`() {
        val machine = createMachine {
            initial(RED)
            state(RED) {
                initial(WALK)
                state(WALK)
                state(WAIT)
            }
        }

        machine.send(SHORT_TIMER)

        assertEquals(listOf(RED, WALK), machine.activeStateIds())
    }

    @Test
    fun `it transitions between nested states`() {
        val machine = createMachine {
            initial(RED)
            state(RED) {
                initial(WALK)
                state(WALK) {
                    transition(on = SHORT_TIMER, target = WAIT)
                }
                state(WAIT)
            }
        }

        machine.send(SHORT_TIMER)

        assertEquals(listOf(RED, WAIT), machine.activeStateIds())
    }

    @Test
    fun `it transitions between nested states with the external strategy by default`() {
        val machine = createMachine {
            initial(RED)
            state(RED) {
                transition(on = TIMER, target = YELLOW)

                initial(WALK)
                state(WALK) {
                    transition(on = TIMER, target = WAIT)
                }
                state(WAIT)
            }
            state(YELLOW)
        }

        machine.send(TIMER)

        assertEquals(listOf(YELLOW), machine.activeStateIds())
    }

    @Test
    fun `it transitions between nested states with the internal strategy`() {
        val machine = createMachine(strategy = KSStrategyType.Internal) {
            initial(RED)
            state(RED) {
                transition(on = TIMER, target = YELLOW)

                initial(WALK)
                state(WALK) {
                    transition(on = TIMER, target = WAIT)
                }
                state(WAIT)
            }
            state(YELLOW)
        }

        machine.send(TIMER)

        assertEquals(listOf(RED, WAIT), machine.activeStateIds())
    }

    @Test
    fun `it transitions between nested states with the internal global strategy`() {
        val machine = createMachine(strategy = KSStrategyType.Internal) {
            initial(RED)
            state(RED) {
                transition(on = TIMER, target = YELLOW)

                initial(WALK)
                state(WALK) {
                    transition(on = TIMER, target = WAIT)

                    initial(GREEN)
                    state(GREEN) {
                        transition(on = TIMER, target = YELLOW)
                    }
                    state(YELLOW)
                }
                state(WAIT)
            }
            state(YELLOW)
        }

        machine.send(TIMER)

        assertEquals(listOf(RED, WALK, YELLOW), machine.activeStateIds())
    }
}