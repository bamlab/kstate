package com.github.bamlab

class Machine(val states: List<MachineState>, val initialState: MachineState) {
    var currentState = initialState

    fun transition(event: MachineEvent) {
      if(event in currentState.allowedTransitions.keys )
        currentState = currentState.allowedTransitions[event] ?: error("The transition could not be found")
    }
}

class MachineBuilder {

    private val states: MutableList<MachineState> = mutableListOf()
    private lateinit var initialState: MachineState
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
