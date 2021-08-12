package tech.bam.kstate.core

import tech.bam.kstate.core.domain.mock.CoffeeMachineEvent.SERVE_COFFEE
import tech.bam.kstate.core.domain.mock.CoffeeMachineStateId.IDLE
import tech.bam.kstate.core.domain.mock.TrafficLightEvent.TIMER
import tech.bam.kstate.core.domain.mock.TrafficLightStateId.RED
import tech.bam.kstate.core.domain.types.StateId
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

fun <TC, E : Any> createTransition(
    on: E,
    target: StateId<TC>
): Transition<TC, Any, E> {
    return Transition(event = on, target = target)
}

fun <TC, E : Any> createTransition(
    on: KClass<E>,
    target: StateId<TC>
): Transition<TC, Any, E> {
    return Transition(eventClass = on, target = target)
}

class TransitionTest {
    @Test
    fun `it register an event`() {
        val transition = createTransition(TIMER, RED)

        assertEquals(TIMER, transition.event)
    }

    @Test
    fun `it register a target`() {
        val transition = createTransition(TIMER, RED)

        assertEquals(RED, transition.target)
    }

    @Test
    fun `it register a parametrized event`() {
        val transition = createTransition(SERVE_COFFEE::class, IDLE)

        assertEquals(SERVE_COFFEE::class, transition.eventClass)
    }
}