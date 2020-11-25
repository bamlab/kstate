package com.github.tpucci

import com.github.tpucci.MachineBuilder.Companion.machine
import kotlin.test.Test
import kotlin.test.assertEquals

sealed class TrafficLightState {
  object GREEN : TrafficLightState()
  object YELLOW : TrafficLightState()
  object RED : TrafficLightState()
}

sealed class TrafficLightEvent {
  object TIMER : TrafficLightEvent()
  object POWER_OUTAGE : TrafficLightEvent()
}

class MachineTest {
  @Test
  fun `it should register states`() {
    // Given
    val testMachine =
        machine<TrafficLightState, TrafficLightEvent> {
          state(TrafficLightState.GREEN) {}
          state(TrafficLightState.YELLOW) {}
          state(TrafficLightState.RED) {}
        }

    // When
    val states = testMachine.states

    // Then
    assertEquals(
        listOf(TrafficLightState.GREEN, TrafficLightState.YELLOW, TrafficLightState.RED), states)
  }

  @Test
  fun `it should register events`() {
    // Given
    val testMachine =
        machine<TrafficLightState, TrafficLightEvent> {
          state(TrafficLightState.GREEN) {
            on(TrafficLightEvent.TIMER) {}
            on(TrafficLightEvent.POWER_OUTAGE) {}
          }
          state(TrafficLightState.YELLOW) {
            on(TrafficLightEvent.TIMER) {}
            on(TrafficLightEvent.POWER_OUTAGE) {}
          }
          state(TrafficLightState.RED) { on(TrafficLightEvent.TIMER) {} }
        }

    // When
    val events = testMachine.events

    // Then
    assertEquals(listOf(TrafficLightEvent.TIMER, TrafficLightEvent.POWER_OUTAGE), events)
  }
}
