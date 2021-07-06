package tech.bam

class MachineTransitionListener(
    val callback: (
        previousActiveStateIds: List<StateId>, nextActiveStateIds: List<StateId>
    ) -> Unit
)