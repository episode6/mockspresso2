package com.episode6.mxo2.plugins.mockito.factories

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.dep
import com.episode6.mxo2.realInstance
import org.junit.jupiter.api.Test

class MockitoAutoFactoryAnnotationTest {

  val mxo = MockspressoBuilder()
    .autoFactoriesByAnnotation<FactoryAnnotation>()
    .build()

  val ro by mxo.realInstance<RealObject>()
  val dep by mxo.dep { Dependency() }

  @Test fun testDependencyIsFromMap() {
    assertThat(ro.dependency).isEqualTo(dep)
  }

  annotation class FactoryAnnotation
  class Dependency
  @FactoryAnnotation interface DependencyFactory {
    fun create(name: String): Dependency
  }

  class RealObject(dependencyFactory: DependencyFactory) {
    val dependency = dependencyFactory.create("real_name")
  }
}
