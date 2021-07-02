package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId
import tech.bam.domain.exception.NoRegisteredStates

open class KSMachine {
    // Protected API
    protected var states: List<KSState> = listOf()
    lateinit var initial: KSStateId
        protected set

    protected fun isInitialInitialized() = ::initial.isInitialized

    // TODO: Implement parallels
    private var parallels: List<KSParallel> = listOf()

    // Public API
    val stateIds: List<KSStateId>
        get() = states.map { it.id }
}

class KSMachineBuilder : KSMachine() {
    fun initial(id: KSStateId) {
        initial = id
    }

    fun state(id: KSStateId) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = KSState(id)
        states = states.toMutableList().also { it.add(newState) }
    }

    fun build() {
        if (!isInitialInitialized()) {
            if (states.isEmpty()) throw NoRegisteredStates()
            initial = states[0].id
        }
    }
}

fun createMachine(init: KSMachineBuilder.() -> Unit): KSMachine {
    val ksMachine = KSMachineBuilder()
    ksMachine.apply(init)

    ksMachine.build()
    return ksMachine
}