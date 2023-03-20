package com.episode6.mockspresso2

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNotNull
import com.episode6.mockspresso2.reflect.dependencyKey
import kotlin.test.Test

class CircularDependencyTest {

  @Test fun testCircularDependency() {
    val mxo = Mockspresso {
      realInstance<A?>()
      realInstance<B?>()
    }

    val c by mxo.realImplementation<C?, C>()

    assertThat {
      c.a
    }.isFailure().hasClass(CircularDependencyError::class)
  }

  @Test fun testWorksWhenChainIsBroken() {
    val mxo = Mockspresso {
      realInstance<A?>()
    }

    val b by mxo.dependency<B?> { B(null) }
    val c by mxo.realImplementation<C?, C>()

    assertThat(c.a).isNotNull()
    assertThat(c.a!!.b).isEqualTo(b)
  }

  @Test fun testCircularDependency_stillFailsOnDynamicDependency() {
    val mxo = Mockspresso {
      realInstance<A?>()
      dependency<B?>(dependencyKey()) { B(get(dependencyKey())) }
    }

    val c by mxo.realImplementation<C?, C>()

    assertThat {
      c.a
    }.isFailure().hasClass(CircularDependencyError::class)
  }

  private class A(val b: B?)
  private class B(val c: C?)
  private class C(val a: A?)
}
