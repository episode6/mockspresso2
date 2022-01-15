package com.episode6.mxo2.plugins.mockito

import assertk.assertThat
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.realInstance
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder

class MockitoFallbackMakerTest {

  val mxo = MockspressoBuilder()
    .fallbackWithMockito()
    .build()

  private val realObject: TestObj by mxo.realInstance()

  @Test fun testFallbackHasMocks() {
    assertThat(realObject).isNotMock()
    assertThat(realObject.dep1).isMock()
    assertThat(realObject.dep2).isMock()
  }

  @Test fun testCanCallMockFunctions() {
    realObject.dep1.doSomething()
    realObject.dep2.doSomethingElse()

    inOrder(realObject.dep1, realObject.dep2) {
      verify(realObject.dep1).doSomething()
      verify(realObject.dep2).doSomethingElse()
    }
  }

  private class TestDepOne {
    fun doSomething() {}
  }

  private interface TestDepTwo {
    fun doSomethingElse()
  }

  private class TestObj(val dep1: TestDepOne, val dep2: TestDepTwo)
}
