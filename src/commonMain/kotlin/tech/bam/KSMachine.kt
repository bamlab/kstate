package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId
import tech.bam.domain.exception.NoRegisteredStates
import tech.bam.domain.exception.UnexpectedError

sealed class KSStrategyType {
    object External : KSStrategyType()
    object Internal : KSStrategyType()
}

open class KSMachine(private val strategy: KSStrategyType) {
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

    fun send(event: KSEvent): Boolean {
        if (strategy == KSStrategyType.External) {
            val transition = currentState().findTransitionOn(event)
            if (transition != null) {
                if (transition.target != null) {
                    val newState = states.find { it.id == transition.target }
                    if (newState != null) {
                        currentStateId = newState.id
                        return true
                    }
                }
            } else {
                return currentState().send(event)
            }
        } else if (strategy == KSStrategyType.Internal) {
            val eventHandled = currentState().send(event)
            if (eventHandled) return true else {
                val transition = currentState().findTransitionOn(event)
                if (transition != null) {
                    if (transition.target != null) {
                        val newState = states.find { it.id == transition.target }
                        if (newState != null) {
                            currentStateId = newState.id
                            return true
                        }
                    }
                }
            }
            return false
        }
        return false
    }

    fun activeStateIds(): List<KSStateId> {
        return listOf(listOf(currentStateId), currentState().activeStateIds()).flatten()
    }
}

class KSMachineBuilder(private val strategy: KSStrategyType) :
    KSMachine(strategy) {
    fun initial(id: KSStateId) {
        initial = id
    }

    fun state(id: KSStateId, init: KSStateBuilder.() -> Unit = {}) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = createState(id, strategy, init)
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

fun createMachine(
    strategy: KSStrategyType = KSStrategyType.External,
    init: KSMachineBuilder.() -> Unit
): KSMachine {
    val ksMachine = KSMachineBuilder(strategy)
    ksMachine.apply(init)

    ksMachine.build()
    return ksMachine
}