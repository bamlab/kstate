package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.UntypedStateId

sealed class TrafficLightStateId : UntypedStateId {
    object TRAFFIC_LIGHT : TrafficLightStateId()
    object RED : TrafficLightStateId()
    object YELLOW : TrafficLightStateId()
    object GREEN : TrafficLightStateId()
}

data class TrafficLightContext(val position: String)

sealed class TrafficLightEvent {
    object TIMER : TrafficLightEvent()
    object SHORT_TIMER : TrafficLightEvent()
}

sealed class PedestrianLightStateId : UntypedStateId {
    object PEDESTRIAN_LIGHT : PedestrianLightStateId()
    object WALK : PedestrianLightStateId()
    object WAIT : PedestrianLightStateId()
}
