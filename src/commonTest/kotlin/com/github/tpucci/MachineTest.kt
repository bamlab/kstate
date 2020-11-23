package com.github.tpucci

import com.github.tpucci.MachineBuilder.Companion.machine
import kotlin.test.Test
import kotlin.test.assertEquals

sealed class TrafficLightState {
  object GREEN : TrafficLightState()
  object YELLOW : TrafficLightState()
  object RED : TrafficLightState()
}

class MachineTest {

  @Test
  fun `it should register states`() {
    // Given
    val testMachine =
        machine<TrafficLightState> {
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
}
