package tech.bam

class State(
    val type: StateType,
    val value: MachineState,
    val transitions: Map<MachineEvent, Transition>,
    val compoundMachine: Machine?
) {
  var history: State? = null
    set(value) {
      value?.apply { this.history = null }
      field = value
    }

  companion object {
    fun from(state: State, compoundMachineState: MachineState?, history: State?): State {
      if (state.type == StateType.HISTORY && history != null) return history
      state.compoundMachine?.let {
        if (compoundMachineState != null) it.reset(Transition(compoundMachineState)) else it.reset()
      }
      return state
    }
  }
}

class StateBuilder(private val machineState: MachineState) {

  val transitionsMap: MutableMap<MachineEvent, Transition> = mutableMapOf()

  var compoundMachine: Machine? = null

  var type: StateType = StateType.DEFAULT

  fun on(event: () -> MachineEvent): TransitionBuilder {
    return TransitionBuilder(this, event())
  }

  fun build(): State {
    return State(type, machineState, transitionsMap, compoundMachine)
  }

  operator fun Machine.unaryPlus() {
    compoundMachine = this
  }

  fun type(newType: StateType) {
    type = newType
  }
}
