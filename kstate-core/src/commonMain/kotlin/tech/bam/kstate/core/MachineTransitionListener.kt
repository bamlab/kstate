package tech.bam.kstate.core

class MachineTransitionListener(
    val callback: (
        previousActiveStateIds: List<StateIdWithContext<out Context>>, nextActiveStateIds: List<StateIdWithContext<out Context>>
    ) -> Unit
)