package tech.bam.domain.mock

import tech.bam.KSEvent
import tech.bam.KSStateId

sealed class TrafficLightStateId : KSStateId {
    object TRAFFIC_LIGHT : TrafficLightStateId()
    object RED : TrafficLightStateId()
    object YELLOW : TrafficLightStateId()
    object GREEN : TrafficLightStateId()
}

sealed class TrafficLightEvent : KSEvent {
    object TIMER : TrafficLightEvent()
    object SHORT_TIMER : TrafficLightEvent()
}

sealed class PedestrianLightStateId : KSStateId {
    object PEDESTRIAN_LIGHT : PedestrianLightStateId()
    object WALK : PedestrianLightStateId()
    object WAIT : PedestrianLightStateId()
}