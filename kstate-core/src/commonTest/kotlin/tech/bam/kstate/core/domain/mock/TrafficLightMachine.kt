package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.domain.types.SimpleStateId

object TrafficLightRootStateId : SimpleStateId

object TRAFFIC_LIGHT : SimpleStateId
sealed class TrafficLightStateId : SimpleStateId {
    object RED : TrafficLightStateId()
    object YELLOW : TrafficLightStateId()
    object GREEN : TrafficLightStateId()
}

sealed class TrafficLightEvent {
    object TIMER : TrafficLightEvent()
    object SHORT_TIMER : TrafficLightEvent()
}

object PEDESTRIAN_LIGHT : SimpleStateId
sealed class PedestrianLightStateId : SimpleStateId {
    object WALK : PedestrianLightStateId()
    object WAIT : PedestrianLightStateId()
}
