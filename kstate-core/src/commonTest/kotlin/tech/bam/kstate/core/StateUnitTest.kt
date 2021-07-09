package tech.bam.kstate.core

import tech.bam.kstate.core.domain.exception.AlreadyRegisteredStateId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

object StubStateId : StateId
object FooStateId : StateId
object BarStateId : StateId
object BazEvent : Event

internal class StateUnitTest {
    @Test
    fun `it registers transitions`() {
        val state = createState(StubStateId) {
            transition()
        }

        assertEquals(1, state.transitions.size)
    }

    @Test
    fun `it registers states`() {
        val state = createState(StubStateId) {
            state(FooStateId)
            state(BarStateId)
        }

        assertEquals(listOf(FooStateId, BarStateId), state.stateIds)
    }

    @Test
    fun `it rejects already registered states`() {
        assertFailsWith<AlreadyRegisteredStateId> {
            createState(StubStateId) {
                state(FooStateId)
                state(BarStateId)
                state(FooStateId)
            }
        }
    }

    @Test
    fun `it registers the initial state`() {
        val state = createState(StubStateId) {
            initial(BarStateId)
            state(FooStateId)
            state(BarStateId)
        }

        assertEquals(BarStateId, state.initial)
    }

    @Test
    fun `it uses the first state as initial state when no initial state is declared`() {
        val state = createState(StubStateId) {
            state(FooStateId)
            state(BarStateId)
        }

        assertEquals(FooStateId, state.initial)
    }

    @Test
    fun `it is a compound state if it has children`() {
        val state = createState(StubStateId) {
            state(FooStateId)
            state(BarStateId)
        }

        assertEquals(true, state.isCompound())
    }

    @Test
    fun `it registers event transitions`() {
        val state = createState(StubStateId) {
            transition(on = BazEvent)
        }

        assertEquals(BazEvent, state.transitions.elementAt(0).event)
    }

    @Test
    fun `it registers type`() {
        val state = createState(StubStateId, type = Type.Parallel) {
            state(FooStateId)
            state(BarStateId)
        }

        assertEquals(Type.Parallel, state.type)
    }
}