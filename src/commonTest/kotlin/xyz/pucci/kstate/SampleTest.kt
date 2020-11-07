package xyz.pucci.kstate

import kotlin.test.Test
import kotlin.test.assertEquals

class SampleTest {

  @Test
  fun testHello() {
    assertEquals("Hello World!", Sample().hello())
  }
}
