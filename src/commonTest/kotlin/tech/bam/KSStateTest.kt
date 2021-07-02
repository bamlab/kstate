package tech.bam

import kotlin.test.Test
import kotlin.test.assertEquals

object StubStateId : KSStateId

class KSStateTest {
    @Test
    fun `it registers transitions`() {
        val state = createState(StubStateId) {
            transition()
        }

        assertEquals(1, state.transitions.size)
    }
}