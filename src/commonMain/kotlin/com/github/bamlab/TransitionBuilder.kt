package com.github.bamlab

class Transition(val machineEvent: MachineEvent, state: MachineState)

class TransitionBuilder(private val stateBuilder: StateBuilder, val event: MachineEvent) {
  infix fun transitionTo(state: MachineState) {
    stateBuilder.transitions += Transition(event, state)
  }
}
