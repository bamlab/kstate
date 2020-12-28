package com.github.bamlab

abstract class MachineState {
    val allowedTransitions: MutableMap<MachineEvent, MachineState> = mutableMapOf()
}
