package com.episode6.mxo2

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNotNull
import com.episode6.mxo2.reflect.dependencyKey
import kotlin.test.Test

class CircularDependencyTest {

  @Test fun testCircularDependency() {
    val mxo = MockspressoBuilder()
      .realInstanceOf<A?>()
      .realInstanceOf<B?>()
      .build()

    val c by mxo.realImplOf<C?, C>()

    assertThat {
      c.a
    }.isFailure().hasClass(CircularDependencyError::class)
  }

  @Test fun testWorksWhenChainIsBroken() {
    val mxo = MockspressoBuilder()
      .realInstanceOf<A?>()
      .build()

    val b by mxo.depOf<B?> { B(null) }
    val c by mxo.realImplOf<C?, C>()

    assertThat(c.a).isNotNull()
    assertThat(c.a!!.b).isEqualTo(b)
  }

  @Test fun testCircularDependency_stillFailsOnDynamicDependency() {
    val mxo = MockspressoBuilder()
      .realInstanceOf<A?>()
      .dependencyOf<B?> { B(get(dependencyKey())) }
      .build()

    val c by mxo.realImplOf<C?, C>()

    assertThat {
      c.a
    }.isFailure().hasClass(CircularDependencyError::class)
  }

  private class A(val b: B?)
  private class B(val c: C?)
  private class C(val a: A?)
}
