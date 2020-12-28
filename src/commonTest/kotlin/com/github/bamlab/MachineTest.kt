package com.github.bamlab

import com.github.bamlab.LightMachineEvents.*
import com.github.bamlab.LightMachineStates.*
import kotlin.test.Test
import kotlin.test.assertEquals

sealed class LightMachineStates : MachineState() {
  object RED : LightMachineStates()
  object GREEN : LightMachineStates()
  object YELLOW : LightMachineStates()
}

sealed class LightMachineEvents : MachineEvent {
  object TIMER : LightMachineEvents()
  object POWER_OUTAGE : LightMachineEvents()
}

class MachineTest {
  private val testMachine =
      machine {
        initial(RED)
        state(GREEN) {
          on { TIMER } transitionTo YELLOW
          on { POWER_OUTAGE } transitionTo YELLOW
        }
        state(YELLOW) { on { TIMER } transitionTo RED }
        state(RED) {
          on { TIMER } transitionTo GREEN
          on { POWER_OUTAGE } transitionTo YELLOW
        }
      }

  @Test
  fun `it should register states`() {
    // When
    val states = testMachine.states

    // Then
    assertEquals(listOf(GREEN, YELLOW, RED), states)
  }

  @Test
  fun `it should have correct initial state`() {
    // When
    val initialState = testMachine.initialState

    // Then
    assertEquals(RED, initialState)
  }

  @Test
  fun `it should transition to correct state`() {
    // When
    val event = TIMER
    testMachine.transition(event = event)


    // Then
    assertEquals(GREEN, testMachine.currentState)
  }
}
