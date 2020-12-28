package com.github.bamlab

import com.github.bamlab.LightMachineEvents.POWER_OUTAGE
import com.github.bamlab.LightMachineEvents.TIMER
import com.github.bamlab.LightMachineStates.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

enum class LightMachineStates : MachineState {
  RED,
  GREEN,
  YELLOW
}

enum class LightMachineEvents : MachineEvent {
  TIMER,
  POWER_OUTAGE
}

class MachineTest {
  // Given
  private lateinit var testMachine: Machine

  // Given
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

  @Test
  fun `it should register history`() {
    // When
    testMachine.transition(TIMER)

    // Then
    assertEquals(RED, testMachine.state.history!!.value)
  }

  @Test
  fun `it should not register more than one history`() {
    // When
    testMachine.transition(TIMER)
    testMachine.transition(TIMER)

    // Then
    assertNull(testMachine.state.history!!.history)
  }
}
