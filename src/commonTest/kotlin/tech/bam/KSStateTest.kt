package tech.bam

import tech.bam.domain.exception.AlreadyRegisteredStateId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

object StubStateId : KSStateId
object FooStateId : KSStateId
object BarStateId : KSStateId

internal class KSStateTest {
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

        assertEquals(state.stateIds, listOf(FooStateId, BarStateId))
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

        assertEquals(state.initial, BarStateId)
    }

    @Test
    fun `it uses the first state as initial state when no initial state is declared`() {
        val state = createState(StubStateId) {
            state(FooStateId)
            state(BarStateId)
        }

        assertEquals(state.initial, FooStateId)
    }

    @Test
    fun `it is a compound state if it has children`() {
        val state = createState(StubStateId) {
            state(FooStateId)
            state(BarStateId)
        }

        assertEquals(state.isCompound(), true)
    }
}