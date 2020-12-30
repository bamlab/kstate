package com.github.bamlab

interface MachineState {
  operator fun plus(compoundMachineState: MachineState): CompoundMachineState {
    if (this is CompoundMachineState)
        return CompoundMachineState(
            this.parent, CompoundMachineState(this.compound, compoundMachineState))
    return CompoundMachineState(this, compoundMachineState)
  }

  val compound: MachineState?
    get() = null
}

data class CompoundMachineState(val parent: MachineState, override val compound: MachineState) :
    MachineState
