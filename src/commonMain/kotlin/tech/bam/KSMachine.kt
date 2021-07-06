package tech.bam

sealed class KSStrategyType {
    object External : KSStrategyType()
    object Internal : KSStrategyType()
}

object KSRoot : KSStateId

fun createMachine(
    strategy: KSStrategyType = KSStrategyType.External,
    init: KSStateBuilder.() -> Unit
): KSState {
    val ksMachine = KSStateBuilder(KSRoot, strategy)
    ksMachine.apply(init)

    ksMachine.build()
    return ksMachine
}