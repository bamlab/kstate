package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId
import tech.bam.domain.exception.NoRegisteredStates
import tech.bam.domain.exception.UnexpectedError

open class KSMachine {
    // Protected API
    protected var states: List<KSState> = listOf()
    lateinit var initial: KSStateId
        protected set

    protected fun isInitialInitialized() = ::initial.isInitialized
    lateinit var currentStateId: KSStateId
        protected set

    // Private API
    private fun currentState(): KSState {
        return states.find { it.id == currentStateId } ?: throw UnexpectedError()
    }

    // TODO: Implement parallels
    private var parallels: List<KSParallel> = listOf()

    // Public API
    val stateIds: List<KSStateId>
        get() = states.map { it.id }

    fun send(event: KSEvent) {
        val transition = currentState().findTransitionOn(event)
        if (transition != null) {
            if (transition.target != null) {
                val newState = states.find { it.id == transition.target }
                if (newState != null) {
                    currentStateId = newState.id
                }
            }
        } else {
            currentState().send(event)
        }
    }

    fun activeStateIds(): List<KSStateId> {
        return listOf(listOf(currentStateId), currentState().activeStateIds()).flatten()
    }
}

class KSMachineBuilder : KSMachine() {
    fun initial(id: KSStateId) {
        initial = id
    }

    fun state(id: KSStateId, init: KSStateBuilder.() -> Unit = {}) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createState(id, init)
        states = states.toMutableList().also { it.add(newState) }
    }

    fun build() {
        if (!isInitialInitialized()) {
            if (states.isEmpty()) throw NoRegisteredStates()
            initial = states[0].id
        }

        currentStateId = initial
    }
}

fun createMachine(init: KSMachineBuilder.() -> Unit): KSMachine {
    val ksMachine = KSMachineBuilder()
    ksMachine.apply(init)

    ksMachine.build()
    return ksMachine
}