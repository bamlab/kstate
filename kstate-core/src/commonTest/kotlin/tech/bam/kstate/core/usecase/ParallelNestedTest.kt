package tech.bam.kstate.core.usecase

import tech.bam.kstate.core.createParallelMachine
import tech.bam.kstate.core.domain.mock.PEDESTRIAN_LIGHT
import tech.bam.kstate.core.domain.mock.PedestrianLightStateId.WAIT
import tech.bam.kstate.core.domain.mock.PedestrianLightStateId.WALK
import tech.bam.kstate.core.domain.mock.TRAFFIC_LIGHT
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightRootStateId
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.GREEN
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.RED
import tech.bam.kstate.core.domain.types.SimpleStateId
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


object Foo : SimpleStateId
object Bar : SimpleStateId

class ParallelNestedTest {
    private val machine = createParallelMachine(
        id = Foo
    ) {
        parallelState(id = TrafficLightRootStateId) {
            state(id = TRAFFIC_LIGHT, initial = RED) {
                state(id = RED) {
                    transition(on = TIMER, target = GREEN)
                }

                state(id = GREEN) {
                    transition(on = TIMER, target = RED)
                }
            }


            state(id = PEDESTRIAN_LIGHT, initial = WAIT) {
                state(id = WAIT) {
                    transition(on = TIMER, target = WALK)
                }

                state(id = WALK) {
                    transition(on = TIMER, target = WAIT)
                }
            }
        }

        state(id = Bar)
    }

    @BeforeTest
    fun startMachine() {
        machine.start()
    }

    @AfterTest
    fun stopMachine() {
        machine.stop()
    }

    @Test
    fun `it transitions inside all nested compound states`() {
        machine.send(TIMER)

        assertEquals(
            listOf(Foo, TrafficLightRootStateId, TRAFFIC_LIGHT, GREEN, PEDESTRIAN_LIGHT, WALK, Bar),
            machine.activeStateIds()
        )
    }
}