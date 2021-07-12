package tech.bam.kstate.core.domain.mock

import tech.bam.kstate.core.Event
import tech.bam.kstate.core.StateId


sealed class TrafficLightStateId : StateId {
    object TRAFFIC_LIGHT : TrafficLightStateId()
    object RED : TrafficLightStateId()
    object YELLOW : TrafficLightStateId()
    object GREEN : TrafficLightStateId()
}

sealed class TrafficLightEvent : Event {
    object TIMER : TrafficLightEvent()
    object SHORT_TIMER : TrafficLightEvent()
}

sealed class PedestrianLightStateId : StateId {
    object PEDESTRIAN_LIGHT : PedestrianLightStateId()
    object WALK : PedestrianLightStateId()
    object WAIT : PedestrianLightStateId()
}