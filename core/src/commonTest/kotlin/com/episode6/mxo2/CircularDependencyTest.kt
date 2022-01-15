package com.episode6.mxo2

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNotNull
import kotlin.test.Test

class CircularDependencyTest {

  @Test fun testCircularDependency() {
    val mxo = MockspressoBuilder()
      .useRealInstanceOf<A?>()
      .useRealInstanceOf<B?>()
      .build()

    val c by mxo.realImplOf<C?, C>()

    assertThat {
      c.a
    }.isFailure().hasClass(CircularDependencyError::class)
  }

  @Test fun testWorksWhenChainIsBroken() {
    val mxo = MockspressoBuilder()
      .useRealInstanceOf<A?>()
      .build()

    val b by mxo.depOf<B?> { B(null) }
    val c by mxo.realImplOf<C?, C>()

    assertThat(c.a).isNotNull()
    assertThat(c.a!!.b).isEqualTo(b)
  }

  private class A(val b: B?)
  private class B(val c: C?)
  private class C(val a: A?)
}
