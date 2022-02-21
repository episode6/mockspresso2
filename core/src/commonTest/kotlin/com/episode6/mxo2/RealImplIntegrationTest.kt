package com.episode6.mxo2

import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEqualTo
import kotlin.test.Test

class RealImplIntegrationTest {

  @Test fun testRealImplOf() {
    val unexpectedTestClass = TestClass()
    val mxo = MockspressoBuilder()
      .dependency { unexpectedTestClass }
      .build()

    val obj by mxo.realImpl<TestInterface, TestClass>()

    assertThat(obj)
      .isInstanceOf(TestClass::class)
      .isNotEqualTo(unexpectedTestClass)
  }

  private interface TestInterface
  private class TestClass : TestInterface
}
