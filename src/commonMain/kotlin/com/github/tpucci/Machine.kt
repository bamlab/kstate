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
}

/**
 * Main class to build a state machine. Use companion object function [machine] to create a state
 * [Machine] instance.
 */
class MachineBuilder<STATE : Any, EVENT : Any> private constructor() : Machine<STATE, EVENT> {

  override lateinit var initialState: STATE

  private val statesMap = mutableMapOf<STATE, StateBuilder<out STATE, EVENT>>()

  override val states: List<STATE>
    get() = statesMap.keys.toList()

  override val events: List<EVENT>
    get() = statesMap.values.flatMap { it.events }.distinct()

  fun <S : STATE> state(state: S, init: StateBuilder<S, EVENT>.() -> Unit) {
    val (k, v) = StateBuilder<S, EVENT>(state).apply(init).build()
    statesMap[k] = v
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
}

class StateBuilder<S, EVENT>(private val state: S) {
  fun build() = Pair(state, this)

  private val transitionsMap = mutableMapOf<EVENT, Unit>()

  val events: List<EVENT>
    get() = transitionsMap.keys.toList()

  fun <E : EVENT> on(event: E, transition: S.() -> Unit) {
    transitionsMap[event] = Unit
  }
}
