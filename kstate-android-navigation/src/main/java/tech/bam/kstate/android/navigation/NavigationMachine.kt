package tech.bam.kstate.android.navigation

import tech.bam.kstate.core.StateBuilder
import tech.bam.kstate.core.StrategyType
import tech.bam.kstate.core.Type
import tech.bam.kstate.core.createContextMachine

fun createNavigationMachine(
    type: Type = Type.Hierarchical,
    init: StateBuilder<FragmentFactory>.() -> Unit
) =
    createContextMachine(
        type = type,
        strategy = StrategyType.Internal,
        init = init
    )

//fun <F : Fragment> StateBuilder<FragmentFactory>.screen(
//    id: Screen<F>,
//    type: Type = Type.Hierarchical,
//    strategy: StrategyType = this.strategy,
//    init: StateBuilder<FragmentFactory>.() -> Unit = {}
//) {
//    this.state()
//}
