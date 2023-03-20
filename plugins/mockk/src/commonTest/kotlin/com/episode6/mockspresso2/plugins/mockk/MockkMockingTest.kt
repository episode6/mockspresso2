package com.episode6.mockspresso2.plugins.mockk

import assertk.all
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNotNull
import com.episode6.mockspresso2.Mockspresso
import com.episode6.mockspresso2.realInstance
import io.mockk.every
import io.mockk.verify
import io.mockk.verifyOrder
import kotlin.test.Test

class MockkMockingTest {

  val mxo = Mockspresso {
    mockk<TestDepOne> {
      every { doSomething() } throws RuntimeException()
    }
  }

  private val realObject: TestObj by mxo.realInstance()
  private val dep2: TestDepTwo by mxo.mockk(relaxed = true)

  private val realWithNullable: TestObjNullable by mxo.realInstance()
  private val nullableDep2: TestDepTwo? by mxo.mockk(relaxed = true)

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

  @Test fun testCanCallMockFunctionsOnNullable() {
    realWithNullable.nullableDep!!.doSomethingElse()

    verify {
      nullableDep2!!.doSomethingElse()
    }
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
