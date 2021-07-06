package tech.bam

import tech.bam.domain.constants.RootStateId

fun createMachine(
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: StateBuilder.() -> Unit
): State = createState(RootStateId, type, strategy, init)