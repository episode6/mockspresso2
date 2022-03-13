package com.episode6.mockspresso2.plugins.mockito

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import com.episode6.mockspresso2.MockspressoBuilder
import com.episode6.mockspresso2.realInstance
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder

class MockitoSpyingTest {

  val mxo = MockspressoBuilder().build()

  private val realObject: TestObj by mxo.realInstance()
  private val dep1: TestDepOne by mxo.spy()
  private val dep2: TestDepTwo by mxo.mock()

  @Test fun testObjectTypes() {
    assertThat(realObject).all {
      isNotMock()

      prop(TestObj::dep1).all {
        isSpy()
        isEqualTo(dep1)

        prop(TestDepOne::dep2).all {
          isMock()
          isNotSpy()
          isEqualTo(dep2)
        }
      }
    }
  }

  @Test fun testCanCallMockFunctions() {
    realObject.dep1.doSomething()

    inOrder(dep1, dep2) {
      verify(dep1).doSomething()
      verify(dep2).touch()
    }
  }

  private class TestDepOne(val dep2: TestDepTwo) {
    fun doSomething() {
      dep2.touch()
    }
  }

  private interface TestDepTwo {
    fun touch()
  }

  private class TestObj(val dep1: TestDepOne)
}
