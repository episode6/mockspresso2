package com.episode6.mockspresso2

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.episode6.mockspresso2.reflect.dependencyKey
import kotlin.test.Test

class CircularDependencyTest {

  @Test fun testCircularDependency() {
    val mxo = MockspressoBuilder()
      .realInstance<A?>()
      .realInstance<B?>()
      .build()

    val c by mxo.realImplementation<C?, C>()

    assertFailure {
      c.a
    }.hasClass(CircularDependencyError::class)
  }

  @Test fun testWorksWhenChainIsBroken() {
    val mxo = MockspressoBuilder()
      .realInstance<A?>()
      .build()

    val b by mxo.dependency<B?> { B(null) }
    val c by mxo.realImplementation<C?, C>()

    assertThat(c.a).isNotNull()
    assertThat(c.a!!.b).isEqualTo(b)
  }

  @Test fun testCircularDependency_stillFailsOnDynamicDependency() {
    val mxo = MockspressoBuilder()
      .realInstance<A?>()
      .dependency<B?>(dependencyKey()) { B(get(dependencyKey())) }
      .build()

    val c by mxo.realImplementation<C?, C>()

    assertFailure {
      c.a
    }.hasClass(CircularDependencyError::class)
  }

  private class A(val b: B?)
  private class B(val c: C?)
  private class C(val a: A?)
}
