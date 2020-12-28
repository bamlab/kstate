package com.github.bamlab

import com.github.bamlab.LightMachineEvents.POWER_OUTAGE
import com.github.bamlab.LightMachineEvents.TIMER
import com.github.bamlab.LightMachineStates.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

sealed class LightMachineStates : MachineState {
  object RED : LightMachineStates()
  object GREEN : LightMachineStates()
  object YELLOW : LightMachineStates()
}

sealed class LightMachineEvents : MachineEvent {
  object TIMER : LightMachineEvents()
  object POWER_OUTAGE : LightMachineEvents()
}

class MachineTest {
  // Given
  lateinit var testMachine: Machine

  @BeforeTest
  fun beforeTest() {
    testMachine =
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
  }

  @Test
  fun `it should register states`() {
    // When
    val registeredStates = testMachine.registeredStates

    // Then
    assertEquals(listOf(GREEN, YELLOW, RED), registeredStates)
  }

  @Test
  fun `it should register events`() {
    // When
    val registeredEvents = testMachine.registeredEvents

    // Then
    assertEquals(listOf(TIMER, POWER_OUTAGE), registeredEvents)
  }

  @Test
  fun `it should register initial state`() {
    // When
    val initialState = testMachine.initialState

    // Then
    assertEquals(RED, initialState.value)
  }

  @Test
  fun `it should transition to other states`() {
    // When
    testMachine.transition(TIMER)

    // Then
    assertEquals(GREEN, testMachine.value)
  }
}
