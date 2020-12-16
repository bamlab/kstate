package com.github.bamlab

class Machine(val states: List<MachineState>)

class MachineBuilder {

  val states: MutableList<MachineState> = mutableListOf()

  fun build(): Machine {
    return Machine(states)
  }

  fun initial(state: MachineState) {
    // TODO
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
