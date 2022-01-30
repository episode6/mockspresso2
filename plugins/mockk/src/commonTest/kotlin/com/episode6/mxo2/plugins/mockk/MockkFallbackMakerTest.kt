package com.episode6.mxo2.plugins.mockk

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.realInstance
import io.mockk.verifyOrder
import kotlin.test.Test

class MockkFallbackMakerTest {

  val mxo = MockspressoBuilder()
    .fallbackWithMockk()
    .build()

  private val realObject: TestObj by mxo.realInstance()


  @Test fun testCanCallMockFunctions() {
    realObject.dep1.doSomething()
    realObject.dep2.doSomethingElse()

    verifyOrder {
      realObject.dep1.doSomething()
      realObject.dep2.doSomethingElse()
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
