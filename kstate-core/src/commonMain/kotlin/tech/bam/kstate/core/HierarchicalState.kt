package tech.bam.kstate.core

import tech.bam.kstate.core.domain.exception.StateNotFound
import tech.bam.kstate.core.domain.types.StateId

open class HierarchicalState<C, PC>(id: StateId<C>, val initialStateId: StateId<*>, context: C) :
    State<C, PC>(id, context),
    CompoundState<C, PC> {
    override var states: List<State<*, C>> = listOf()
    private var currentStateId: StateId<*>? = null

    fun currentState(): State<*, C> {
        val currentState = states.find { it.id == currentStateId }
        if (currentState == null) throw StateNotFound(this.currentStateId!!, id)
        else return currentState
    }

    private fun transitionTo(stateId: StateId<*>) {
        this.currentStateId = stateId
        val currentState = this.currentState()
        currentState.start()
    }

    override fun start() {
        super.start()
        transitionTo(initialStateId)
    }

    override fun stop() {
        super.stop()
        this.currentStateId = null
    }

    /**
     * The id of the previous state.
     * Can be `null` if this state is the initial state
     * or if this state is a first destination of its parent.
     */
    var history: StateId<*>? = null

    override fun send(event: Any): Boolean {
        val handled = handleEventWithTransition(event) || handleEventWithChild(event)
        if (handled) runAlwaysTransitions()
        return handled
    }

    private fun runAlwaysTransitions() {
        currentState().transitions
            .filter { it.isAlways }
            .forEach {
                if (it.cond == null || it.cond.invoke(context)) {
                    val matchingState = stateIds.find { stateId -> stateId == it.target }
                    if (matchingState != null) {
                        this.currentStateId = it.target
                    }
                }
            }
    }

    private fun handleEventWithTransition(event: Any): Boolean {
        val matchingTransition = currentState().findTransitionOn(event, context)
        if (matchingTransition != null) {
            val matchingStateId = stateIds.find { it == matchingTransition.target }
            if (matchingStateId != null) {
                transitionTo(matchingStateId)
                matchingTransition.effect?.invoke(event, context)
                return true
            }
        }
        return false
    }

    private fun handleEventWithChild(event: Any): Boolean {
        if (currentState() is CompoundState<*, *>) {
            // TODO: Secure this unchecked cast.
            @Suppress("UNCHECKED_CAST")
            return (currentState() as CompoundState<*, C>).send(event)
        }
        return false
    }


    override fun activeStateIds(): List<StateId<*>> {
        return listOf(id) + currentState().activeStateIds()
    }
}

class HierarchicalStateBuilder<C, PC>(id: StateId<C>, initialStateId: StateId<*>, context: C) :
    HierarchicalState<C, PC>(id, initialStateId, context),
    CompoundStateBuilder<C, PC>,
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

    fun build(): HierarchicalState<C, PC> = this
}