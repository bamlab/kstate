package com.github.bamlab

class Machine(private val states: List<State>) {
  val registeredEvents: List<MachineEvent>
    get() =
        states.flatMap { it.transitions.map { transition -> transition.machineEvent } }.distinct()

  val registeredStates: List<MachineState>
    get() = states.map { it.machineState }
}

class MachineBuilder {
  private val states: MutableList<State> = mutableListOf()

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
