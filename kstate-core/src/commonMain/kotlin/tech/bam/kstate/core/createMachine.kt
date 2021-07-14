package tech.bam.kstate.core

import tech.bam.kstate.core.domain.constants.RootStateId

/**
 * Creates a *finite state machine* with a context.
 *
 * @param type the type of the state machine. Either [Type.Hierarchical] or [Type.Parallel].
 * @param strategy the strategy of the state machine.
 *  Either [StrategyType.External] or [StrategyType.Internal].
 *  *kstate* introduces [StrategyType] concept. Default to [StrategyType.External].
 *  When set to [StrategyType.Internal], events are handled by children first, and
 *  then the compound state.
 * @param init use *kstate*'s DSL to declare your state machine.
 * @return returns a [State]
 */
fun <C : Any> createContextMachine(
    id: StateId = RootStateId(),
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: StateBuilder<C>.() -> Unit
): State<C> = createContextState(id, type, strategy, init).also { it.start() }

fun createMachine(
    id: StateId = RootStateId(),
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: StateBuilder<Nothing>.() -> Unit
): State<Nothing> = createState(id, type, strategy, init).also { it.start() }