package tech.bam.kstate.android.navigation

import tech.bam.kstate.core.DeprecatedStateBuilder
import tech.bam.kstate.core.Type
import tech.bam.kstate.core.createContextMachine
import tech.bam.kstate.core.domain.types.Strategy

fun createNavigationMachine(
    type: Type = Type.Hierarchical,
    init: DeprecatedStateBuilder<FragmentFactory>.() -> Unit
) =
    createContextMachine(
        type = type,
        strategy = Strategy.Internal,
        init = init
    )

//fun <F : Fragment> StateBuilder<FragmentFactory>.screen(
//    id: Screen<F>,
//    type: Type = Type.Hierarchical,
//    strategy: Strategy = this.strategy,
//    init: StateBuilder<FragmentFactory>.() -> Unit = {}
//) {
//    this.state()
//}
