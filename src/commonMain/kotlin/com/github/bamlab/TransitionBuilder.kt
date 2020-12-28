package com.github.bamlab

class Transition(val state: MachineState)

class TransitionBuilder(private val stateBuilder: StateBuilder, val event: MachineEvent) {
  infix fun transitionTo(state: MachineState) {
    stateBuilder.transitionsMap[event] = Transition(state)
  }
}
