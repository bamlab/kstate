package tech.bam

sealed class StrategyType {
    object External : StrategyType()
    object Internal : StrategyType()
}