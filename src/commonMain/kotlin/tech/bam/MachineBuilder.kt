package tech.bam

import tech.bam.State.Companion.from

class Machine(val initialState: State, private val statesMap: Map<MachineState, State>) {
  var state: State = initialState
    set(value) {
      value.history = field
      field = value
    }

  val registeredEvents: List<MachineEvent>
    get() = statesMap.values.flatMap { it.transitions.keys }.distinct()

  val registeredStates: List<MachineState>
    get() = statesMap.values.map { it.value }

  val value: MachineState
    get() = state.value

  fun transition(event: MachineEvent) {
    if (state.compoundMachine != null) {
      state.compoundMachine!!.transition(event)
    }

    val transition = state.transitions[event] ?: return
    val nextState =
        statesMap[transition.targetState]?.let { from(it, transition.compoundState, state.history) }
            ?: return
    state = nextState
  }

  fun reset(initialTransition: Transition) {
    val nextState =
        statesMap[initialTransition.targetState]?.let {
          from(it, initialTransition.compoundState, state.history)
        }
            ?: return
    state = nextState
    state.history = null
  }

  fun reset() {
    state = initialState
    state.history = null
  }
}

class MachineBuilder {
  private lateinit var initialState: MachineState
  private val statesMap: MutableMap<MachineState, State> = mutableMapOf()

  fun build(): Machine {
    return Machine(statesMap[initialState] ?: StateBuilder(initialState).build(), statesMap)
  }

  fun initial(state: MachineState) {
    initialState = state
  }

  fun state(state: MachineState, setup: StateBuilder.() -> Unit = {}) {
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
