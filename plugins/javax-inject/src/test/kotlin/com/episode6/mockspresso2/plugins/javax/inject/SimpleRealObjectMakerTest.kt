package com.episode6.mockspresso2.plugins.javax.inject

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.episode6.mockspresso2.Mockspresso
import com.episode6.mockspresso2.dependency
import com.episode6.mockspresso2.realInstance
import org.junit.jupiter.api.Test
import javax.inject.Inject

class SimpleRealObjectMakerTest {

  @Test fun testConstructorInject() {
    val mxo = Mockspresso { makeRealObjectsUsingJavaxInjectRules() }
    val ro: ConstructorInject by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.d1).isEqualTo(d1)
    assertThat(ro.d2).isEqualTo(d2)
  }

  @Test fun testPropertyInject() {
    val mxo = Mockspresso { makeRealObjectsUsingJavaxInjectRules() }
    val ro: PropertiesInject by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.d1).isEqualTo(d1)
    assertThat(ro.d2).isEqualTo(d2)
  }

  @Test fun testPropertySetInject() {
    val mxo = Mockspresso { makeRealObjectsUsingJavaxInjectRules() }
    val ro: PropertiesSetInject by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.d1).isEqualTo(d1)
    assertThat(ro.d2).isEqualTo(d2)
  }

  @Test fun testPropertyFieldInject() {
    val mxo = Mockspresso { makeRealObjectsUsingJavaxInjectRules() }
    val ro: PropertiesFieldInject by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.d1).isEqualTo(d1)
    assertThat(ro.d2).isEqualTo(d2)
  }

  @Test fun testJavaFieldInject() {
    val mxo = Mockspresso { makeRealObjectsUsingJavaxInjectRules() }
    val ro: FieldInjectTestClass by mxo.realInstance()
    val d1 by mxo.dependency { FieldInjectTestClass.Dep1() }
    val d2 by mxo.dependency { FieldInjectTestClass.Dep2() }

    assertThat(ro.d1).isEqualTo(d1)
    assertThat(ro.d2).isEqualTo(d2)
  }

  @Test fun testMethodInject() {
    val mxo = Mockspresso { makeRealObjectsUsingJavaxInjectRules() }
    val ro: MethodInject by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.d1).isEqualTo(d1)
    assertThat(ro.d2).isEqualTo(d2)
  }

  private class Dependency1
  private class Dependency2

  private class ConstructorInject @Inject constructor(val d1: Dependency1, val d2: Dependency2)
  private class PropertiesInject @Inject constructor() {
    @Inject lateinit var d1: Dependency1
    @Inject lateinit var d2: Dependency2
  }

  private class PropertiesSetInject @Inject constructor() {
    @set:Inject lateinit var d1: Dependency1
    @set:Inject lateinit var d2: Dependency2
  }

  private class PropertiesFieldInject @Inject constructor() {
    @field:Inject lateinit var d1: Dependency1
    @field:Inject lateinit var d2: Dependency2
  }

  private class MethodInject @Inject constructor() {
    lateinit var d1: Dependency1
    lateinit var d2: Dependency2
    @Inject fun doInject(d1: Dependency1, d2: Dependency2) {
      this.d1 = d1
      this.d2 = d2
    }
  }
}
