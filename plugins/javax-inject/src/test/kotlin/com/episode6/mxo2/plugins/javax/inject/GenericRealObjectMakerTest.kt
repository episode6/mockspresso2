package com.episode6.mxo2.plugins.javax.inject

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

    assertThat(ro.a).isEqualTo(depA)
    assertThat(ro.b).isEqualTo(depB)
    assertThat(ro.c).isEqualTo(depC)
  }

  @Test fun testSimpleSubclassDirectGeneric() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: SubClass<String, Int, Long> by mxo.realInstance()
    val depC: String by mxo.depOf { "hi" }
    val depB: Int by mxo.depOf { 4 }
    val depA: Long by mxo.depOf { 12L }

    assertThat(ro.a).isEqualTo(depA)
    assertThat(ro.b).isEqualTo(depB)
    assertThat(ro.c).isEqualTo(depC)
  }

  private open class GenericObj<A : Any, B : Any, C : Any> @Inject constructor() {
    @Inject lateinit var a: A
    @Inject lateinit var b: B
    @Inject lateinit var c: C
  }

  private open class SubClass<C: Any, B: Any, A: Any> @Inject constructor() : GenericObj<A, B, C>()
}
