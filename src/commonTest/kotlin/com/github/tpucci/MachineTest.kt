package com.github.tpucci

import com.github.tpucci.MachineBuilder.Companion.machine
import com.github.tpucci.TrafficLightEvent.POWER_OUTAGE
import com.github.tpucci.TrafficLightEvent.TIMER
import com.github.tpucci.TrafficLightState.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
        initial(RED)
        state(GREEN) { on(TIMER to YELLOW, POWER_OUTAGE to YELLOW) }
        state(YELLOW) { on(TIMER to RED) }
        state(RED) { on(TIMER to GREEN, POWER_OUTAGE to YELLOW) }
      }

  @Test
  fun `it should register states`() {
    // When
    val states = testMachine.states

    // Then
    assertEquals(listOf(GREEN, YELLOW, RED), states)
  }

  @Test
  fun `it should register events`() {
    // When
    val events = testMachine.events

    // Then
    assertEquals(listOf(TIMER, POWER_OUTAGE), events)
  }

  @Test
  fun `it should register the initial state`() {
    // When
    val initialState = testMachine.initialState

    // Then
    assertEquals(RED, initialState)
  }

  @Test
  fun `it should not retain the previous history to prevent memory leaks`() {
    // When
    val nextState = testMachine.transition(testMachine.initialState, TIMER)
    val followingState = testMachine.transition(nextState, TIMER)

    // Then
    assertNull(followingState.history!!.history)
  }
}
