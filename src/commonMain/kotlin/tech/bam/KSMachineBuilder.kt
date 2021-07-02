package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId
import tech.bam.domain.exception.NoRegisteredStates

open class KSMachine {
    val stateIds: List<KSStateId>
        get() = states.map { it.id }
    protected var states: List<KSState> = listOf()
    private var parallels: List<KSParallel> = listOf()
    var initial: KSStateId? = null

    fun state(id: KSStateId) {
        if (states.find { it.id == id } != null) {
            throw AlreadyRegisteredStateId(id)
        }

        val newState = KSState(id)
        states = states.toMutableList().also { it.add(newState) }
    }


}

class KSMachineBuilder : KSMachine() {
    fun build() {
        if (initial == null) {
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