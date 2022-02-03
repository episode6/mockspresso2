package com.episode6.mxo2.plugins.javax.inject

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.depOf
import com.episode6.mxo2.reflect.typeToken
import org.junit.jupiter.api.Test
import javax.inject.Inject

class JavaxInjectTest {

  val mxo = MockspressoBuilder().build()

  private val dep1 by mxo.depOf { Dependency1() }
  private val dep2 by mxo.depOf { Dependency2() }

  @Test fun testPropertyInject() {
    val ro = PropertiesInject()

    mxo.inject(ro)

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  @Test fun testPropertySetInject() {
    val ro = PropertiesSetInject()

    mxo.inject(ro)

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  @Test fun testPropertyFieldInject() {
    val ro = PropertiesFieldInject()

    mxo.inject(ro)

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  @Test fun testMethodInject() {
    val ro = MethodInject()

    mxo.inject(ro)

    assertThat(ro.d1).isEqualTo(dep1)
    assertThat(ro.d2).isEqualTo(dep2)
  }

  @Test fun testGenericFail() {
    val ro = GenericInject<Dependency1, Dependency2>()

    assertThat { mxo.inject(ro) }.isFailure().hasClass(IllegalArgumentException::class)
  }

  @Test fun testGenericSuccess() {
    val ro = GenericInject<Dependency1, Dependency2>()

    mxo.inject(ro, typeToken())

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
