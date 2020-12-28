package com.github.bamlab

class Machine(val states: List<MachineState>, val initialState: MachineState)

class MachineBuilder {

  val states: MutableList<MachineState> = mutableListOf()
  lateinit var initialState: MachineState
  fun build(): Machine {
    return Machine(states, initialState)
  }

  fun initial(state: MachineState) {
    initialState = state
  }

  fun state(state: MachineState, setup: StateBuilder.() -> Unit) {
    val stateBuilder = StateBuilder(state)
    stateBuilder.setup()
    states += stateBuilder.build()
  }
}

fun machine(setup: MachineBuilder.() -> Unit): Machine {
  val machineBuilder = MachineBuilder()
  machineBuilder.setup()
  return machineBuilder.build()
}
