package com.github.bamlab

class Machine(val initialState: State, private val states: List<State>) {
  val registeredEvents: List<MachineEvent>
    get() =
        states.flatMap { it.transitions.map { transition -> transition.machineEvent } }.distinct()

  val registeredStates: List<MachineState>
    get() = states.map { it.value }
}

class MachineBuilder {
  private lateinit var initialState: MachineState
  private val statesMap: MutableMap<MachineState, State> = mutableMapOf()

  fun build(): Machine {
    return Machine(
        statesMap[initialState] ?: StateBuilder(initialState).build(), statesMap.values.toList())
  }

  fun initial(state: MachineState) {
    initialState = state
  }

  fun state(state: MachineState, setup: StateBuilder.() -> Unit) {
    val stateBuilder = StateBuilder(state)
    stateBuilder.setup()
    statesMap[state] = stateBuilder.build()
  }
}

fun machine(setup: MachineBuilder.() -> Unit): Machine {
  val machineBuilder = MachineBuilder()
  machineBuilder.setup()
  return machineBuilder.build()
}
