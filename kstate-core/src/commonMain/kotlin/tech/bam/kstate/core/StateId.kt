package tech.bam.kstate.core

interface StateId<T, C>

typealias UntypedStateId = StateId<Any, Any>

/*
data class StateIdWithContextPair<C : Any>(
    val stateIdWithContext: StateId,
    val context: C?
)*/
