package com.github.bamlab

class StateBuilder(private val machineState: MachineState) {
  fun on(event: () -> MachineEvent) {}

  // TODO: Change returned type to State
  fun build(): MachineState {
    return machineState
  }
}

infix fun Unit.transitionTo(state: MachineState) {}
