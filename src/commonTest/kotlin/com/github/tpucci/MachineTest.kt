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
  // Given
  private val testMachine =
      machine<TrafficLightState, TrafficLightEvent> {
        initial(TrafficLightState.RED)
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

  @Test
  fun `it should register states`() {
    // When
    val states = testMachine.states

    // Then
    assertEquals(
        listOf(TrafficLightState.GREEN, TrafficLightState.YELLOW, TrafficLightState.RED), states)
  }

  @Test
  fun `it should register events`() {
    // When
    val events = testMachine.events

    // Then
    assertEquals(listOf(TrafficLightEvent.TIMER, TrafficLightEvent.POWER_OUTAGE), events)
  }

  @Test
  fun `it should register the initial state`() {
    // When
    val initialState = testMachine.initialState

    // Then
    assertEquals(TrafficLightState.RED, initialState)
  }
}
