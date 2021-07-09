package tech.bam.kstate.core

sealed class StrategyType {
    object External : StrategyType()
    object Internal : StrategyType()
}