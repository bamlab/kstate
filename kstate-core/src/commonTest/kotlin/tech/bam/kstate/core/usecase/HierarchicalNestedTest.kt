package tech.bam.kstate.core.usecase

import tech.bam.kstate.core.createMachine
import tech.bam.kstate.core.domain.mock.PedestrianLightStateId.WAIT
import tech.bam.kstate.core.domain.mock.PedestrianLightStateId.WALK
import tech.bam.kstate.core.domain.mock.TRAFFIC_LIGHT
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.SHORT_TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HierarchicalNestedTest {
    private val machine = createMachine(
        id = TRAFFIC_LIGHT,
        initial = RED
    ) {
        state(id = RED) {
            transition(on = TIMER, target = GREEN)
        }

        state(id = GREEN, initial = WALK) {
            transition(on = TIMER, target = YELLOW)

            state(id = WALK) {
                transition(on = SHORT_TIMER, target = WAIT)
            }

            state(id = WAIT)
        }

        state(id = YELLOW) {
            transition(on = TIMER, target = RED)
        }
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

        machine.send(SHORT_TIMER)

        assertEquals(listOf(TRAFFIC_LIGHT, GREEN, WAIT), machine.activeStateIds())
    }
}