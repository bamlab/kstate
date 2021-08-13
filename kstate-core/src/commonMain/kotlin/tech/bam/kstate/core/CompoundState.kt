package tech.bam.kstate.core

import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId
import tech.bam.kstate.core.domain.types.StateId

interface CompoundState<C, PC> {
    var states: List<State<*, C>>

    val stateIds: List<StateId<*>>
        get() = states.map { it.id }

    fun send(event: Any): Boolean
}

interface CompoundStateBuilder<C, PC> : CompoundState<C, PC> {
    fun state(
        id: StateId<Any>,
        initial: StateId<*>,
        init: HierarchicalStateBuilder<Any, C>.() -> Unit
    ) {
        val newState = HierarchicalStateBuilder<Any, C>(
            id = id,
            context = Unit,
            initialStateId = initial
        ).apply(init).build()
        addState(newState)
    }

    fun addState(state: State<*, C>) {
        if (states.find { it.id == state.id } != null) {
            throw AlreadyRegisteredStateId(state.id)
        }

        states = states.toMutableList().also { it.add(state) }
    }
}
