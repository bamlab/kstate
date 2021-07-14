package tech.bam.kstate.core

interface StateId

data class StateIdWithContextPair<C : Any>(
    val stateIdWithContext: StateId,
    val context: C?
)