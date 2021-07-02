package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId
import tech.bam.domain.exception.NoRegisteredStates
import tech.bam.domain.exception.UnexpectedError

sealed class KSStrategyType {
    object External : KSStrategyType()
    object Internal : KSStrategyType()
}

object KSRoot : KSStateId

open class KSMachine(strategy: KSStrategyType) : KSState(KSRoot, strategy) {
    override fun currentState(): KSState {
        return states.find { it.id == currentStateId } ?: throw UnexpectedError()
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
        if (initial == null) {
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