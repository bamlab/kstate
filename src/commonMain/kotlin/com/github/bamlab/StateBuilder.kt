package com.github.bamlab

class State(val machineState: MachineState, val transitions: List<Transition>)

class StateBuilder(private val machineState: MachineState) {

  val transitions: MutableList<Transition> = mutableListOf()

  fun on(event: () -> MachineEvent): TransitionBuilder {
    return TransitionBuilder(this, event())
  }

  fun build(): State {
    return State(machineState, transitions)
  }
}
