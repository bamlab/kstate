package tech.bam.kstate.core

sealed class Type {
    object Hierarchical : Type()
    object Parallel : Type()
}