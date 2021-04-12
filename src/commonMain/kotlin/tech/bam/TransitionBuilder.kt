package tech.bam

class Transition(private val state: MachineState) {
  val compoundState: MachineState?
    get() {
      return state.compound
    }

  val targetState: MachineState
    get() {
      return if (state is CompoundMachineState) state.parent else state
    }
}

class TransitionBuilder(private val stateBuilder: StateBuilder, val event: MachineEvent) {
  infix fun transitionTo(state: MachineState) {
    stateBuilder.transitionsMap[event] = Transition(state)
  }
}
