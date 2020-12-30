package com.github.bamlab

import com.github.bamlab.LightMachineEvents.POWER_OUTAGE
import com.github.bamlab.LightMachineEvents.TIMER
import com.github.bamlab.LightMachineStates.*
import com.github.bamlab.PedestrianMachineEvents.PED_COUNTDOWN
import com.github.bamlab.PedestrianMachineStates.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

enum class LightMachineStates : MachineState {
  RED,
  GREEN,
  YELLOW
}

enum class PedestrianMachineEvents : MachineEvent {
  PED_COUNTDOWN
}

enum class PedestrianMachineStates : MachineState {
  WALK,
  WAIT,
  STOP,
  BLINKING
}

enum class LightMachineEvents : MachineEvent {
  TIMER,
  POWER_OUTAGE
}

class MachineTest {
  // Given
  private lateinit var lightMachine: Machine

  // Given
  @BeforeTest
  fun beforeTest() {
    val pedestrianMachine =
        machine {
          initial(WALK)
          state(WALK) { on { PED_COUNTDOWN } transitionTo WAIT }
          state(WAIT) { on { PED_COUNTDOWN } transitionTo STOP }
          state(STOP) {}
          state(BLINKING) {}
        }

    lightMachine =
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
            +pedestrianMachine
          }
        }
  }

  @Test
  fun `it should register states`() {
    // When
    val registeredStates = lightMachine.registeredStates

    // Then
    assertEquals(listOf(GREEN, YELLOW, RED), registeredStates)
  }

  @Test
  fun `it should register events`() {
    // When
    val registeredEvents = lightMachine.registeredEvents

    // Then
    assertEquals(listOf(TIMER, POWER_OUTAGE), registeredEvents)
  }

  @Test
  fun `it should register initial state`() {
    // When
    val initialState = lightMachine.initialState

    // Then
    assertEquals(RED, initialState.value)
  }

  @Test
  fun `it should transition to other states`() {
    // When
    lightMachine.transition(TIMER)

    // Then
    assertEquals(GREEN, lightMachine.value)
  }

  @Test
  fun `it should register history`() {
    // When
    lightMachine.transition(TIMER)

    // Then
    assertEquals(RED, lightMachine.state.history!!.value)
  }

  @Test
  fun `it should not register more than one history`() {
    // When
    lightMachine.transition(TIMER)
    lightMachine.transition(TIMER)

    // Then
    assertNull(lightMachine.state.history!!.history)
  }

  @Test
  fun `it should transition in compound machine`() {
    // When
    lightMachine.transition(PED_COUNTDOWN)

    // Then
    assertEquals(WAIT, lightMachine.state.compoundMachine!!.state.value)
  }
}
