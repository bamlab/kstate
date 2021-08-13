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
).apply(init).build()

fun createMachine(
    id: StateId<Any>,
    initial: StateId<*>,
    init: HierarchicalStateBuilder<Any, Unit>.() -> Unit
) = HierarchicalStateBuilder<Any, Unit>(
    id = id,
    context = Unit,
    initialStateId = initial
).apply(init).build()

fun <Context> createParallelMachine(
    id: StateId<Context>,
    context: Context,
    init: ParallelStateBuilder<Context, Unit>.() -> Unit
) = ParallelStateBuilder<Context, Unit>(
    id = id,
    context = context
).apply(init).build()

fun createParallelMachine(
    id: StateId<Any>,
    init: ParallelStateBuilder<Any, Unit>.() -> Unit
) = ParallelStateBuilder<Any, Unit>(
    id = id,
    context = Unit
).apply(init).build()
