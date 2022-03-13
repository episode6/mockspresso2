package com.episode6.mockspresso2.plugins.javax.inject

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import com.episode6.mockspresso2.MockspressoBuilder
import com.episode6.mockspresso2.dependency
import com.episode6.mockspresso2.reflect.typeToken
import org.junit.jupiter.api.Test
import javax.inject.Inject

class JavaxInjectTest {

  val mxo = MockspressoBuilder().build()

  private val dep1 by mxo.dependency { Dependency1() }
  private val dep2 by mxo.dependency { Dependency2() }

  @Test fun testPropertyInject() {
    val ro = PropertiesInject()

    mxo.injectNow(ro)

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  @Test fun testPropertySetInject() {
    val ro = PropertiesSetInject()

    mxo.injectNow(ro)

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  @Test fun testPropertyFieldInject() {
    val ro = PropertiesFieldInject()

    mxo.injectNow(ro)

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  @Test fun testMethodInject() {
    val ro = MethodInject()

    mxo.injectNow(ro)

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  @Test fun testGenericFail() {
    val ro = GenericInject<Dependency1, Dependency2>()

    assertThat { mxo.injectNow(ro) }.isFailure().hasClass(IllegalArgumentException::class)
  }

  @Test fun testGenericSuccess() {
    val ro = GenericInject<Dependency1, Dependency2>()

    mxo.injectNow(ro, typeToken())

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  private class Dependency1
  private class Dependency2
  private class PropertiesInject {
    @Inject lateinit var d1: Dependency1
    @Inject lateinit var d2: Dependency2
  }

  private class PropertiesSetInject {
    @set:Inject lateinit var d1: Dependency1
    @set:Inject lateinit var d2: Dependency2
  }

  private class PropertiesFieldInject {
    @field:Inject lateinit var d1: Dependency1
    @field:Inject lateinit var d2: Dependency2
  }

  private class MethodInject {
    lateinit var d1: Dependency1
    lateinit var d2: Dependency2
    @Inject fun doInject(d1: Dependency1, d2: Dependency2) {
      this.d1 = d1
      this.d2 = d2
    }
  }

  private open class GenericInject<A: Any, B: Any> {
    @Inject lateinit var d1: A
    @Inject lateinit var d2: B
  }
}
