package tech.bam

sealed class Type {
    object Hierarchical : Type()
    object Parallel : Type()
}