package com.episode6.mockspresso2

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNotNull
import com.episode6.mockspresso2.reflect.MultipleQualifierError
import org.junit.jupiter.api.Test
import javax.inject.Qualifier
import kotlin.reflect.full.createInstance

class QualifierIntegrationTest {

  @Test fun testSimpleWorkingCaseWithQualifier() {
    val mxo = MockspressoBuilder().build()
    val objUnderTest: SomeObjectWithQualifier by mxo.realInstance()
    val dep1 by mxo.dependency { SomeDependency1() }
    val dep2 by mxo.dependency(qualifier = SomeQualifier::class.createInstance()) { SomeDependency1() }

    assertThat(objUnderTest).isNotNull()
    assertThat(objUnderTest.dependency1).isEqualTo(dep1)
    assertThat(objUnderTest.dependency2).isEqualTo(dep2)
  }

  @Test fun testMultipleQualifiers() {
    val mxo = MockspressoBuilder().build()
    val objUnderTest: SomeBadObjectWithMultipleQualifiers by mxo.realInstance()

    assertThat { objUnderTest.doSomething() }
      .isFailure()
      .hasClass(MultipleQualifierError::class)
  }

  private class SomeDependency1
  @Qualifier private annotation class SomeQualifier
  @Qualifier private annotation class SomeQualifier2
  private class SomeObjectWithQualifier(
    val dependency1: SomeDependency1,
    @SomeQualifier val dependency2: SomeDependency1
  )

  private class SomeBadObjectWithMultipleQualifiers(@SomeQualifier @SomeQualifier2 val dependency1: SomeDependency1) {
    fun doSomething() {}
  }
}
