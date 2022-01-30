package com.episode6.mxo2.plugins.javax.inject

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.depOf
import com.episode6.mxo2.realInstance
import org.junit.jupiter.api.Test
import javax.inject.Inject

class GenericRealObjectMakerTest {

  @Test fun testSimpleDirectGeneric() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: GenericObj<String, Int, Long> by mxo.realInstance()
    val depA: String by mxo.depOf { "hi" }
    val depB: Int by mxo.depOf { 4 }
    val depC: Long by mxo.depOf { 12L }

    assertThat(ro.a).all {
      isEqualTo(depA)
      isEqualTo("hi")
    }
    assertThat(ro.b).all {
      isEqualTo(depB)
      isEqualTo(4)
    }
    assertThat(ro.c).all {
      isEqualTo(depC)
      isEqualTo(12L)
    }
  }

  @Test fun testSimpleSubclassDirectGeneric() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: SubClass<String, Int, Long> by mxo.realInstance()
    val depC: String by mxo.depOf { "hi" }
    val depB: Int by mxo.depOf { 4 }
    val depA: Long by mxo.depOf { 12L }

    assertThat(ro.a).all {
      isEqualTo(depA)
      isEqualTo(12L)
    }
    assertThat(ro.b).all {
      isEqualTo(depB)
      isEqualTo(4)
    }
    assertThat(ro.c).all {
      isEqualTo(depC)
      isEqualTo("hi")
    }
  }

  @Test fun testSimpleDirectGeneric_methodInject() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: GenericObjMethodInject<String, Int, Long> by mxo.realInstance()
    val depA: String by mxo.depOf { "hi" }
    val depB: Int by mxo.depOf { 4 }
    val depC: Long by mxo.depOf { 12L }

    assertThat(ro.a).all {
      isEqualTo(depA)
      isEqualTo("hi")
    }
    assertThat(ro.b).all {
      isEqualTo(depB)
      isEqualTo(4)
    }
    assertThat(ro.c).all {
      isEqualTo(depC)
      isEqualTo(12L)
    }
  }

  @Test fun testSimpleSubclassDirectGeneric_methodInject() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: SubClassMethodInject<String, Int, Long> by mxo.realInstance()
    val depC: String by mxo.depOf { "hi" }
    val depB: Int by mxo.depOf { 4 }
    val depA: Long by mxo.depOf { 12L }

    assertThat(ro.a).all {
      isEqualTo(depA)
      isEqualTo(12L)
    }
    assertThat(ro.b).all {
      isEqualTo(depB)
      isEqualTo(4)
    }
    assertThat(ro.c).all {
      isEqualTo(depC)
      isEqualTo("hi")
    }
  }

  private open class GenericObj<A : Any, B : Any, C : Any> @Inject constructor() {
    @Inject lateinit var a: A
    @Inject lateinit var b: B
    @Inject lateinit var c: C
  }

  private open class SubClass<C : Any, B : Any, A : Any> @Inject constructor() : GenericObj<A, B, C>()

  private open class GenericObjMethodInject<A : Any, B : Any, C : Any> @Inject constructor() {
    lateinit var a: A
    lateinit var b: B
    lateinit var c: C
    @Inject fun inject(a: A, b: B, c: C) {
      this.a = a
      this.b = b
      this.c = c
    }
  }

  private open class SubClassMethodInject<C : Any, B : Any, A : Any> @Inject constructor() :
    GenericObjMethodInject<A, B, C>()
}
