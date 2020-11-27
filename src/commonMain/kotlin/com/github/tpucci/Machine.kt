package com.github.tpucci

import com.github.tpucci.MachineBuilder.Companion.machine

/** The interface returned by [machine]. */
interface Machine<STATE, EVENT> {
  /** List of possible states. */
  val states: List<STATE>
  /** Initial state. */
  val initialState: STATE
  /** List of accepted events. */
  val events: List<EVENT>

  /**
   * Determines the next state given the current `state` and sent `event`.
   * @param state The current State instance or state value
   * @param event The event that was sent at the current state
   */
  fun transition(state: State<STATE, EVENT>, event: EVENT): State<STATE, EVENT>
  fun transition(state: STATE, event: EVENT): State<STATE, EVENT>
}

interface State<STATE, EVENT> {
  fun transition(event: EVENT): STATE

  val stateValue: STATE
  var history: State<STATE, EVENT>?
  val events: List<EVENT>
}

/**
 * Main class to build a state machine. Use companion object function [machine] to create a state
 * [Machine] instance.
 */
class MachineBuilder<STATE : Any, EVENT : Any> private constructor() : Machine<STATE, EVENT> {

  override lateinit var initialState: STATE

  private val statesMap = mutableMapOf<STATE, State<STATE, EVENT>>()

  override val states: List<STATE>
    get() = statesMap.keys.toList()

  override val events: List<EVENT>
    get() = statesMap.values.flatMap { it.events }.distinct()

  fun <S : STATE> state(state: S, init: StateBuilder<EVENT, STATE>.() -> Unit) {
    statesMap[state] = StateBuilder<EVENT, STATE>(state).apply(init).build()
  }

  fun <S : STATE> initial(state: S) {
    if (this::initialState.isInitialized) {
      throw Error("Initial state already set to $initialState. Can not reinitialize it to $state")
    }
    initialState = state
  }

  fun build() = this as Machine<STATE, EVENT>

  companion object {
    fun <STATE : Any, EVENT : Any> machine(init: MachineBuilder<STATE, EVENT>.() -> Unit) =
        MachineBuilder<STATE, EVENT>().apply(init).build()
  }

  override fun transition(state: State<STATE, EVENT>, event: EVENT): State<STATE, EVENT> {
    return statesMap[state.transition(event)]!!.also {
      state.history = null
      it.history = state
    }
  }

  override fun transition(stateValue: STATE, event: EVENT): State<STATE, EVENT> {
    statesMap[stateValue].let {
      if (it == null) {
        throw Error("Cannot transition from not found state `$stateValue`")
      } else return transition(it, event)
    }
  }
}

class StateBuilder<EVENT, STATE>(
    override val stateValue: STATE, override var history: State<STATE, EVENT>? = null
) : State<STATE, EVENT> {
  fun build() = this as State<STATE, EVENT>

  private lateinit var transitionsMap: Map<EVENT, STATE>

  override val events: List<EVENT>
    get() = transitionsMap.keys.toList()

  fun on(vararg transitions: Pair<EVENT, STATE>) {
    transitionsMap = mapOf(*transitions)
  }

  override fun transition(event: EVENT): STATE {
    transitionsMap[event].let {
      if (it == null) {
        throw Error("Cannot find transition for event `$event` for state `$stateValue`")
      } else return it
    }
  }
}
