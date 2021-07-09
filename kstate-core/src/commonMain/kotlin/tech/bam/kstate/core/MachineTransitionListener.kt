package tech.bam.kstate.core

class MachineTransitionListener(
    val callback: (
        previousActiveStateIds: List<StateId>, nextActiveStateIds: List<StateId>
    ) -> Unit
)