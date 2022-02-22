package com.episode6.mxo2.plugins.dagger2

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.dependency
import com.episode6.mxo2.realInstance
import dagger.Lazy
import org.junit.jupiter.api.Test
import javax.inject.Inject

class Dagger2LazyMakerTest {

  @Test fun testObjWithProviders() {
    val mxo = MockspressoBuilder().dagger2LazySupport().build()
    val ro: ObjWithProviders by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.d1.get()).isEqualTo(d1)
    assertThat(ro.d2.get()).isEqualTo(d2)
  }

  @Test fun testGenericObjWithProviders() {
    val mxo = MockspressoBuilder().dagger2LazySupport().build()
    val ro: GenericObjWithProviders<Dependency1, Dependency2> by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.a.get()).isEqualTo(d1)
    assertThat(ro.b.get()).isEqualTo(d2)
  }

  @Test fun testGenericObjWithProviders_inject() {
    val mxo = MockspressoBuilder().dagger2LazySupport().makeRealObjectsUsingDagger2Rules().build()
    val ro: GenericObjWithProvidersInject<Dependency1, Dependency2> by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.a.get()).isEqualTo(d1)
    assertThat(ro.b.get()).isEqualTo(d2)
  }

  @Test fun testSubclassGenericObjWithProviders_inject() {
    val mxo = MockspressoBuilder().dagger2LazySupport().makeRealObjectsUsingDagger2Rules().build()
    val ro: SubclassOfGenericWithProviders<Dependency1, Dependency2> by mxo.realInstance()
    val d1 by mxo.dependency { Dependency1() }
    val d2 by mxo.dependency { Dependency2() }

    assertThat(ro.b.get()).isEqualTo(d1)
    assertThat(ro.a.get()).isEqualTo(d2)
  }

  private class Dependency1
  private class Dependency2

  private class ObjWithProviders(
    val d1: Lazy<Dependency1>,
    val d2: Lazy<Dependency2>
  )

  private open class GenericObjWithProviders<A : Any, B : Any>(val a: Lazy<A>, val b: Lazy<B>)
  private open class GenericObjWithProvidersInject<A : Any, B : Any> @Inject constructor() {
    @Inject lateinit var a: Lazy<A>
    @Inject lateinit var b: Lazy<B>
  }

  private class SubclassOfGenericWithProviders<B : Any, A : Any> @Inject constructor() :
    GenericObjWithProvidersInject<A, B>()
}
