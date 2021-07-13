package tech.bam.kstate.core

interface TransitionListener

class MachineTransitionListener(
    val callback: (
        previousActiveStateIds: List<StateIdWithContext<out Context>>, nextActiveStateIds: List<StateIdWithContext<out Context>>
    ) -> Unit
) : TransitionListener

class MachineTransitionWithContextListener(
    val callback: (
        prev: List<StateIdWithContextPair<*>>, next: List<StateIdWithContextPair<*>>
    ) -> Unit
) : TransitionListener