package tech.bam.kstate.core

import tech.bam.kstate.core.domain.types.StateId

open class ParallelState<C, PC>(id: StateId<C>, context: C) :
    State<C, PC>(id, context),
    CompoundState<C, PC> {
    override var states: List<State<*, C>> = listOf()

    override fun start() {
        super.start()
        states.forEach { state -> state.start() }
    }

    override fun stop() {
        states.forEach { state -> state.stop() }
        super.stop()
    }

    override fun send(event: Any): Boolean {
        return states.any {
            if (it is CompoundState<*, *>) {
                // TODO: Secure this unchecked cast.
                @Suppress("UNCHECKED_CAST")
                return (it as CompoundState<*, C>).send(event)
            }
            return false
        }
    }
}

class ParallelStateBuilder<C, PC>(id: StateId<C>, context: C) :
    ParallelState<C, PC>(id, context), CompoundStateBuilder<C, PC>,
    IStateBuilder<C, PC> {

    fun <Context> state(
        id: StateId<Context>,
        context: Context,
        init: StateBuilder<Context, C>.() -> Unit = {}
    ) {
        val newState = StateBuilder<Context, C>(id, context).apply(init).build()
        addState(newState)
    }

    fun state(
        id: StateId<Any>,
        init: StateBuilder<Any, C>.() -> Unit = {}
    ) {
        val newState = StateBuilder<Any, C>(id, Unit).apply(init).build()
        addState(newState)
    }
    
    fun build(): ParallelState<C, PC> = this
}