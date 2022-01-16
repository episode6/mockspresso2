package com.episode6.mxo2.plugins.mockito

import assertk.all
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.prop
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.realInstance
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.inOrder

class MockitoMockingTest {

  val mxo = MockspressoBuilder()
    .defaultMock<TestDepOne> {
      on { doSomething() } doThrow RuntimeException()
    }
    .build()

  private val realObject: TestObj by mxo.realInstance()
  private val dep2: TestDepTwo by mxo.mock()

  @Test fun testFallbackHasMocks() {
    assertThat(realObject).all {
      isNotMock()
      prop(TestObj::dep1).isMock()
      prop(TestObj::dep2).all {
        isMock()
        isEqualTo(dep2)
      }
    }
  }

  @Test fun testCanCallMockFunctions() {
    assertThat { realObject.dep1.doSomething() }
      .isFailure()
      .hasClass(RuntimeException::class)
    realObject.dep2.doSomethingElse()

    inOrder(realObject.dep1, realObject.dep2) {
      verify(realObject.dep1).doSomething()
      verify(dep2).doSomethingElse()
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
