package com.github.tpucci

interface Machine<STATE> {
  val states: List<STATE>
}

class MachineBuilder<STATE> private constructor() : Machine<STATE> {

  private val statesMap = mutableMapOf<STATE, Unit>()

  override val states: List<STATE>
    get() = statesMap.keys.toList()

  fun <S : STATE> state(state: S, init: StateBuilder<S>.() -> Unit) {
    val (k, v) = StateBuilder<S>(state).apply(init).build()
    statesMap[k] = v
  }

  fun build() = this as Machine<STATE>

  companion object {
    fun <STATE : Any> machine(init: MachineBuilder<STATE>.() -> Unit) =
        MachineBuilder<STATE>().apply(init).build()
  }
}

class StateBuilder<S>(private val state: S) {
  fun build() = Pair(state, Unit)
}
