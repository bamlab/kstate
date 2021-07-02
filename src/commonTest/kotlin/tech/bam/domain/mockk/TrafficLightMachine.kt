package tech.bam.domain.mockk

import tech.bam.KSEvent
import tech.bam.KSStateId

sealed class TrafficLightStateId : KSStateId {
    object RED : TrafficLightStateId()
    object YELLOW : TrafficLightStateId()
    object GREEN : TrafficLightStateId()
}

sealed class TrafficLightEvent : KSEvent {
    object TIMER : TrafficLightEvent()
    object SHORT_TIMER : TrafficLightEvent()
}

sealed class PedestrianLightStateId : KSStateId {
    object WALK : PedestrianLightStateId()
    object WAIT : PedestrianLightStateId()
}