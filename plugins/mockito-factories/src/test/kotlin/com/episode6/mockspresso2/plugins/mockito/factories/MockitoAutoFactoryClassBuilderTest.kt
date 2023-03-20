package com.episode6.mockspresso2.plugins.mockito.factories

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.episode6.mockspresso2.Mockspresso
import com.episode6.mockspresso2.dependency
import com.episode6.mockspresso2.realInstance
import org.junit.jupiter.api.Test

class MockitoAutoFactoryClassBuilderTest {

  val mxo = Mockspresso {
    autoFactory<DependencyFactory>()
  }

  val ro by mxo.realInstance<RealObject>()
  val dep by mxo.dependency { Dependency() }

  @Test fun testDependencyIsFromMap() {
    assertThat(ro.dependency).isEqualTo(dep)
  }

  class Dependency
  interface DependencyFactory {
    fun create(name: String): Dependency
  }

  class RealObject(dependencyFactory: DependencyFactory) {
    val dependency = dependencyFactory.create("real_name")
  }
}
