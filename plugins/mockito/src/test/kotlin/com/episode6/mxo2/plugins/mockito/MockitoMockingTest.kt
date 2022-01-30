package com.episode6.mxo2.plugins.mockito

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.realInstance
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.verify

class MockitoMockingTest {

  val mxo = MockspressoBuilder()
    .defaultMock<TestDepOne> {
      on { doSomething() } doThrow RuntimeException()
    }
    .build()

  private val realObject: TestObj by mxo.realInstance()
  private val dep2: TestDepTwo by mxo.mock()

  private val realWithNullable: TestObjNullable by mxo.realInstance()
  private val nullableDep2: TestDepTwo? by mxo.mock()

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

  @Test fun testCanCallMockFunctionsOnNullable() {
    realWithNullable.nullableDep!!.doSomethingElse()

    verify(nullableDep2!!).doSomethingElse()

    assertThat(realWithNullable.nullableDep).all {
      isNotNull()
      isEqualTo(nullableDep2)
    }
  }

  private class TestDepOne {
    fun doSomething() {}
  }

  private interface TestDepTwo {
    fun doSomethingElse()
  }

  private class TestObj(val dep1: TestDepOne, val dep2: TestDepTwo)
  private class TestObjNullable(val nullableDep: TestDepTwo?)
}
