package tech.bam.kstate.core

interface StateId : StateIdWithContext<Context>

interface StateIdWithContext<C : Context>

data class StateIdWithContextPair<C : Context>(
    val stateIdWithContext: StateIdWithContext<C>,
    val context: C
)
