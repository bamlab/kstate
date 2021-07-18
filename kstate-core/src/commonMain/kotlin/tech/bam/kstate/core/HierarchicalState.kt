package tech.bam.kstate.core

import tech.bam.kstate.core.domain.constants.RootStateId
import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId

open class HierarchicalState<T, C, PC>(id: StateId<T, C>, val initialStateId: StateId<T, *>) :
    State<T, C, PC>(id),
    CompoundState<T, C, PC> {
    override var states: List<State<T, *, C>> = listOf()
}

class HierarchicalStateBuilder<T, C, PC>(id: StateId<T, C>, initialStateId: StateId<T, *>) :
    HierarchicalState<T, C, PC>(id, initialStateId) {

    fun state(
        id: StateId<T, *>,
        init: StateBuilder<T, *, C>.() -> Unit = {}
    ) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createState(id, init)
        states = states.toMutableList().also { it.add(newState) }
    }

    fun context(context: C) {
        this.context = context
    }

    fun onEntry(onEntry: () -> Unit) {
        this.onEntry = onEntry
    }

    fun onExit(onExit: () -> Unit) {
        this.onExit = onExit
    }

    fun build(): HierarchicalState<T, C, PC> = this
}

fun createHState(
    initial: StateId<Any, *>,
    init: HierarchicalStateBuilder<Any, Any, Any>.() -> Unit
) =
    HierarchicalStateBuilder<Any, Any, Any>(
        id = RootStateId(),
        initialStateId = initial
    ).apply(init).also { it.context(Unit) }.build()


fun <C> createHStateWithContext(
    initial: StateId<Any, *>,
    init: HierarchicalStateBuilder<Any, C, Any>.() -> Unit
) =
    HierarchicalStateBuilder<Any, C, Any>(
        id = RootStateId(),
        initialStateId = initial
    ).apply(init).build()