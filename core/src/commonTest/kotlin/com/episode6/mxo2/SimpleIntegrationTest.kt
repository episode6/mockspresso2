package com.episode6.mxo2

import assertk.assertThat
import assertk.assertions.*
import kotlin.test.Test

@Suppress("UNUSED_VARIABLE") // We need to hold refs to some values to set up our test cases. If these were real tests the vars would not be unused.
class SimpleIntegrationTest {
  @Test fun testSimpleWorkingCase() {
    val mxo = MockspressoBuilder().build()
    val objUnderTest: SomeObject by mxo.realInstance()
    val dep1 by mxo.depOf { SomeDependency1() }
    val dep2 by mxo.depOf { SomeDependency2() }

    assertThat(objUnderTest).isNotNull()
    assertThat(objUnderTest.dependency1).isEqualTo(dep1)
    assertThat(objUnderTest.dependency2).isEqualTo(dep2)
  }

  @Test fun testAccessDepFirst() {
    val mxo = MockspressoBuilder().build()
    val objUnderTest: SomeObject by mxo.realInstance()
    val dep1 by mxo.depOf { SomeDependency1() }
    val dep2 by mxo.depOf { SomeDependency2() }

    dep2.doSomething()

    assertThat(objUnderTest).isNotNull()
    assertThat(objUnderTest.dependency1).isEqualTo(dep1)
    assertThat(objUnderTest.dependency2).isEqualTo(dep2)
  }

  @Test fun testFailsWithMultipleDepsOfSameKey() {
    val mxo = MockspressoBuilder().build()
    val objUnderTest: SomeObject by mxo.realInstance()
    val dep1 by mxo.depOf { SomeDependency1() }

    assertThat {
      val dep2 by mxo.depOf { SomeDependency1() }
    }.isFailure().hasClass(DependencyAlreadyMappedError::class)
  }

  @Test fun testFailsWithMultipleDepsOfSameKey_real_over_dep() {
    val mxo = MockspressoBuilder().build()
    val objUnderTest: SomeObject by mxo.realInstance()
    val dep1 by mxo.depOf { SomeDependency1() }

    assertThat {
      val dep2: SomeDependency1 by mxo.realInstance()
    }.isFailure().hasClass(DependencyAlreadyMappedError::class)
  }

  @Test fun testMissingDeps() {
    val mxo = MockspressoBuilder().build()
    val objUnderTest: SomeObject by mxo.realInstance()

    assertThat {
      objUnderTest.doSomething()
    }.isFailure().hasClass(NoFallbackMakerProvidedError::class)
  }

  @Test fun testMultipleSameDepGetsSameInstance() {
    val mxo = MockspressoBuilder().build()
    val objUnderTest: SomeBadObjectWithSameDepMultipleTimes by mxo.realInstance()
    val dep by mxo.depOf { SomeDependency1() }

    assertThat(objUnderTest).isNotNull()
    assertThat(objUnderTest.dependency1).isEqualTo(objUnderTest.dependency2)
    assertThat(objUnderTest.dependency2).isEqualTo(dep)
  }

  @Test fun testCreateRealObjectGetsNewInstances() {
    val mxo = MockspressoBuilder().build()

    val obj1: SomeDependency1 = mxo.createRealObject()
    val obj2: SomeDependency1 = mxo.createRealObject()

    assertThat(obj1).isNotEqualTo(obj2)
  }

  @Test fun testCreateRealObjectIsntCachedAsDep() {
    val mxo = MockspressoBuilder().build()

    val obj1: SomeDependency1 = mxo.createRealObject()

    assertThat {
      mxo.findDependency<SomeDependency1>()
    }.isFailure().hasClass(NoFallbackMakerProvidedError::class)
  }

  private class SomeObject(val dependency1: SomeDependency1, val dependency2: SomeDependency2) {
    fun doSomething() {}
  }

  private class SomeDependency1
  private class SomeDependency2 {
    fun doSomething() {}
  }

  private class SomeBadObjectWithSameDepMultipleTimes(
    val dependency1: SomeDependency1,
    val dependency2: SomeDependency1
  )
}
