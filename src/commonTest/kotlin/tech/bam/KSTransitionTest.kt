package tech.bam

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals

object FooEvent : KSEvent
object FooState : KSStateId

class KSTransitionTest {
    @Test
    fun `it registers the event`() {
        val transition = createTransition(
            on = FooEvent
        )

        assertEquals(FooEvent, transition.event)
    }

    @Test
    fun `it registers the target`() {
        val transition = createTransition(
            target = FooState
        )

        assertEquals(FooState, transition.target)
    }

    @Test
    fun `it registers the effect`() {
        val effect = mockk<() -> Unit>()
        every { effect() } returns Unit
        
        val transition = createTransition(target = FooState) {
            effect()
        }

        transition.effect()
        verify { effect() }
        confirmVerified(effect)
    }
}