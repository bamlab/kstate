package tech.bam.kstate.core

import tech.bam.kstate.core.domain.types.StateId

fun <Context> createMachine(
    id: StateId<Context>,
    context: Context,
    initial: StateId<*>,
    init: HierarchicalStateBuilder<Context, Unit>.() -> Unit
) = HierarchicalStateBuilder<Context, Unit>(
    id = id,
    context = context,
    initialStateId = initial
).apply(init)
