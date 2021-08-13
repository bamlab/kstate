package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.domain.types.SimpleStateId
import tech.bam.kstate.core.domain.types.StateId

data class CoffeeMachineContext(
    val coffeeStock: Int,
    val waterStock: Int,
)

object RootCoffeeMachineStateId : StateId<CoffeeMachineContext>

sealed class CoffeeMachineStateId : SimpleStateId {
    object IDLE : CoffeeMachineStateId()
    object SERVING : CoffeeMachineStateId()
    object OFF : CoffeeMachineStateId()
}

sealed class CoffeeMachineEvent {
    object POWER_ON : CoffeeMachineEvent()
    object POWER_OFF : CoffeeMachineEvent()
    class SERVE_COFFEE(val coffeeAmount: Int) : CoffeeMachineEvent()
}
