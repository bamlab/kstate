package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import tech.bam.kstate.core.domain.mock.PedestrianLightStateId.*
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.SHORT_TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.*
import kotlin.test.Test
import kotlin.test.assertEquals

class StateMachineFunctionalTest {
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
        val machine = createMachine(strategy = StrategyType.Internal) {
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
        val machine = createMachine(strategy = StrategyType.Internal) {
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

    @Test
    fun `its compound state is in initial state when reentering a compound state`() {
        val machine = createMachine {
            initial(RED)
            state(RED) {
                transition(on = TIMER, target = GREEN)

                initial(WALK)
                state(WALK) {
                    transition(on = SHORT_TIMER, target = WAIT)
                }
                state(WAIT)
            }
            state(GREEN) {
                transition(on = TIMER, target = RED)
            }
        }

        machine.send(SHORT_TIMER)
        machine.send(TIMER)
        machine.send(TIMER)

        assertEquals(listOf(RED, WALK), machine.activeStateIds())
    }

    @Test
    fun `it transitions in all parallel states`() {
        val machine = createMachine(type = Type.Parallel) {
            state(TRAFFIC_LIGHT) {
                initial(RED)
                state(RED) {
                    transition(on = TIMER, target = GREEN)
                }
                state(GREEN) {
                    transition(on = TIMER, target = RED)
                }
            }
            state(PEDESTRIAN_LIGHT) {
                initial(WAIT)
                state(WAIT) {
                    transition(on = TIMER, target = WALK)
                }
                state(WALK) {
                    transition(on = TIMER, target = WAIT)
                }
            }
        }

        machine.send(TIMER)

        assertEquals(
            listOf(TRAFFIC_LIGHT, GREEN, PEDESTRIAN_LIGHT, WALK),
            machine.activeStateIds()
        )
    }

    @Test
    fun `it calls listeners with previous and next active state ids`() {
        val effect =
            mockk<(previousActiveStateIds: List<StateIdWithContext<out Context>>, nextActiveStateIds: List<StateIdWithContext<out Context>>) -> Unit>()
        every { effect(any(), any()) } returns Unit

        val machine = createMachine(type = Type.Parallel) {
            state(TRAFFIC_LIGHT) {
                initial(RED)
                state(RED) {
                    transition(on = TIMER, target = GREEN)
                }
                state(GREEN) {
                    transition(on = TIMER, target = RED)
                }
            }
            state(PEDESTRIAN_LIGHT) {
                initial(WAIT)
                state(WAIT) {
                    transition(on = TIMER, target = WALK)
                }
                state(WALK) {
                    transition(on = TIMER, target = WAIT)
                }
            }
        }

        machine.onTransition { prev, next ->
            effect(prev, next)
        }

        machine.send(TIMER)

        verify {
            effect(
                listOf(TRAFFIC_LIGHT, RED, PEDESTRIAN_LIGHT, WAIT),
                listOf(TRAFFIC_LIGHT, GREEN, PEDESTRIAN_LIGHT, WALK)
            )
        }
        confirmVerified(effect)
    }
}