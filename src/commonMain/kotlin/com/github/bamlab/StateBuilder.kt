package com.github.bamlab

class StateBuilder(private val machineState: MachineState) {

    fun on(vararg events: MachineEvent): Map<MachineState, MachineEvent> {
        val newTransitions = mutableMapOf<MachineState, MachineEvent>()
        events.map { newTransitions[machineState] = it }
        return newTransitions
    }

    // TODO: Change returned type to State
    fun build(): MachineState {
        return machineState
    }
}

infix fun Map<MachineState, MachineEvent>.transitionTo(state: MachineState) {
    map { it.key.allowedTransitions[it.value] = state }
}


