package com.github.bamlab

class StateBuilder(private val machineState: MachineState) {

  infix fun on(event: () -> MachineEvent) = Pair(machineState, event())


  // TODO: Change returned type to State
  fun build(): MachineState {
    return machineState
  }
}

infix fun Pair<MachineState, MachineEvent>.transitionTo(state: MachineState) {
  first.allowedTransitions[second] = state
}
