package tech.bam.kstate.core

import tech.bam.kstate.core.domain.constants.RootStateId

/**
 * Creates a *finite state machine*.
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
fun createMachine(
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: StateBuilder<Context>.() -> Unit
): State<Context> = createState(RootStateId(), type, strategy, init)

fun <C : Context> createMachine(
    id: StateIdWithContext<C>,
    type: Type = Type.Hierarchical,
    strategy: StrategyType = StrategyType.External,
    init: StateBuilder<C>.() -> Unit
): State<C> = createState(id, type, strategy, init)