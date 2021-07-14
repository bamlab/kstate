package tech.bam.kstate.core

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals

object FooEvent : Event
object FooState : StateId
interface BarContext
object ContextualBarState : StateId

class TransitionUnitTest {
    @Test
    fun `it registers the event`() {
        val transition = createTransition<Nothing>(
            target = FooState,
            on = FooEvent
        )

        assertEquals(FooEvent, transition.event)
    }

    @Test
    fun `it registers the target`() {
        val transition = createTransition<Nothing>(
            target = FooState
        )

        assertEquals(FooState, transition.target)
    }

    @Test
    fun `it registers the effect`() {
        val effect = mockk<() -> Unit>()
        every { effect() } returns Unit

        val transition = createTransition<Nothing>(target = FooState) {
            effect()
        }

        transition.effect(object : Event {})
        verify { effect() }
        confirmVerified(effect)
    }

    @Test
    fun `it registers the effect on a contextual state`() {
        val effect = mockk<() -> Unit>()
        every { effect() } returns Unit

        val transition = createTransition<Nothing>(target = ContextualBarState) {
            effect()
            object : BarContext {}
        }

        transition.effect(object : Event {})
        verify { effect() }
        confirmVerified(effect)
    }
}