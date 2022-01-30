package com.episode6.mxo2.plugins.mockk

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.realInstance
import io.mockk.verifyOrder
import kotlin.test.Test

class MockkSpyingTest {

  val mxo = MockspressoBuilder().build()

  private val realObject: TestObj by mxo.realInstance()
  private val dep1: TestDepOne by mxo.spyk()
  private val dep2: TestDepTwo by mxo.mockk(relaxed = true)

  @Test fun testObjectTypes() {
    assertThat(realObject).all {
      prop(TestObj::dep1).all {
        isEqualTo(dep1)
        prop(TestDepOne::dep2).all {
          isEqualTo(dep2)
        }
      }
    }
  }

  @Test fun testCanCallMockFunctions() {
    realObject.dep1.doSomething()

    verifyOrder {
      dep1.doSomething()
      dep2.touch()
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
