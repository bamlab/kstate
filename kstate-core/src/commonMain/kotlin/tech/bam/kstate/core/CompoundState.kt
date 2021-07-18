package tech.bam.kstate.core

interface CompoundState<T, C, PC> {
    var states: List<State<T, *, C>>
    
    val stateIds: List<StateId<T, *>>
        get() = states.map { it.id }
}