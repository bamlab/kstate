package tech.bam.kstate.core

import tech.bam.kstate.core.domain.types.StateId

interface CompoundState<C, PC> {
    var states: List<State<*, C>>

    val stateIds: List<StateId<*>>
        get() = states.map { it.id }

    fun send(event: Any): Boolean
}

interface CompoundStateBuilder<C, PC> : CompoundState<C, PC>
