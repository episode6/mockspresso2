package com.episode6.mxo2.plugins.mockk

import assertk.all
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.prop
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.realInstance
import io.mockk.every
import io.mockk.verifyOrder
import kotlin.test.Test

class MockkMockingTest {

  val mxo = MockspressoBuilder()
    .defaultMockk<TestDepOne> {
      every { doSomething() } throws RuntimeException()
    }
    .build()

  private val realObject: TestObj by mxo.realInstance()
  private val dep2: TestDepTwo by mxo.mockk(relaxed = true)

  @Test fun testFallbackHasMocks() {
    assertThat(realObject.dep2).isEqualTo(dep2)
  }

  @Test fun testCanCallMockFunctions() {
    assertThat { realObject.dep1.doSomething() }
      .isFailure()
      .hasClass(RuntimeException::class)
    realObject.dep2.doSomethingElse()

    verifyOrder {
      realObject.dep1.doSomething()
      dep2.doSomethingElse()
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
