/*
package tech.bam.kstate.core

interface TransitionListener<C : Any>

class MachineTransitionListener<C : Any>(
    val callback: (
        previousActiveStateIds: List<StateId>, nextActiveStateIds: List<StateId>
    ) -> Unit
) : TransitionListener<C>

class MachineTransitionWithContextListener<C : Any>(
    val callback: (
        prev: List<StateIdWithContextPair<C>>, next: List<StateIdWithContextPair<C>>
    ) -> Unit
) : TransitionListener<C>*/
