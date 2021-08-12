package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.domain.types.SimpleStateId

sealed class TrafficLightStateId : SimpleStateId {
    object RED : TrafficLightStateId()
    object YELLOW : TrafficLightStateId()
    object GREEN : TrafficLightStateId()
}

sealed class TrafficLightEvent {
    object TIMER : TrafficLightEvent()
    object SHORT_TIMER : TrafficLightEvent()
}

sealed class PedestrianLightStateId : SimpleStateId {
    object PEDESTRIAN_LIGHT : PedestrianLightStateId()
    object WALK : PedestrianLightStateId()
    object WAIT : PedestrianLightStateId()
}
