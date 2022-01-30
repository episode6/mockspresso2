package com.episode6.mxo2.plugins.javax.inject

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.depOf
import com.episode6.mxo2.realInstance
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.inject.Provider

class ProviderMakerTest {

  @Test fun testObjWithProviders() {
    val mxo = MockspressoBuilder().javaxProviderSupport().build()
    val ro: ObjWithProviders by mxo.realInstance()
    val d1 by mxo.depOf { Dependency1() }
    val d2 by mxo.depOf { Dependency2() }

    assertThat(ro.d1.get()).isEqualTo(d1)
    assertThat(ro.d2.get()).isEqualTo(d2)
  }

  @Test fun testGenericObjWithProviders() {
    val mxo = MockspressoBuilder().javaxProviderSupport().build()
    val ro: GenericObjWithProviders<Dependency1, Dependency2> by mxo.realInstance()
    val d1 by mxo.depOf { Dependency1() }
    val d2 by mxo.depOf { Dependency2() }

    assertThat(ro.a.get()).isEqualTo(d1)
    assertThat(ro.b.get()).isEqualTo(d2)
  }

  @Test fun testGenericObjWithProviders_inject() {
    val mxo = MockspressoBuilder().javaxProviderSupport().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: GenericObjWithProvidersInject<Dependency1, Dependency2> by mxo.realInstance()
    val d1 by mxo.depOf { Dependency1() }
    val d2 by mxo.depOf { Dependency2() }

    assertThat(ro.a.get()).isEqualTo(d1)
    assertThat(ro.b.get()).isEqualTo(d2)
  }

  @Test fun testSubclassGenericObjWithProviders_inject() {
    val mxo = MockspressoBuilder().javaxProviderSupport().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: SubclassOfGenericWithProviders<Dependency1, Dependency2> by mxo.realInstance()
    val d1 by mxo.depOf { Dependency1() }
    val d2 by mxo.depOf { Dependency2() }

    assertThat(ro.b.get()).isEqualTo(d1)
    assertThat(ro.a.get()).isEqualTo(d2)
  }

  private class Dependency1
  private class Dependency2

  private class ObjWithProviders(
    val d1: Provider<Dependency1>,
    val d2: Provider<Dependency2>
  )

  private open class GenericObjWithProviders<A : Any, B : Any>(val a: Provider<A>, val b: Provider<B>)
  private open class GenericObjWithProvidersInject<A : Any, B : Any> @Inject constructor() {
    @Inject lateinit var a: Provider<A>
    @Inject lateinit var b: Provider<B>
  }
  private class SubclassOfGenericWithProviders<B: Any, A: Any> @Inject constructor() : GenericObjWithProvidersInject<A, B>()
}
