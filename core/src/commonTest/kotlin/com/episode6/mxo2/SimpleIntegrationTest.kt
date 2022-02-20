package com.episode6.mxo2

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import com.episode6.mxo2.reflect.dependencyKey
import io.mockk.spyk
import io.mockk.verify
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

    val obj1: SomeDependency1 = mxo.createNow()
    val obj2: SomeDependency1 = mxo.createNow()

    assertThat(obj1).isNotEqualTo(obj2)
  }

  @Test fun testCreateRealObjectIsntCachedAsDep() {
    val mxo = MockspressoBuilder().build()

    val obj1: SomeDependency1 = mxo.createNow()

    assertThat {
      mxo.findNow<SomeDependency1>()
    }.isFailure().hasClass(NoFallbackMakerProvidedError::class)
  }

  @Test fun testInterceptSpyk() {
    val mxo = MockspressoBuilder()
      .dependencyOf { SomeDependency1() }
      .build()

    val dep2: SomeDependency2 by mxo.realInstance { spyk(it) }
    val objUnderTest: SomeObject by mxo.realInstance()

    assertThat(dep2).isEqualTo(objUnderTest.dependency2)
    objUnderTest.dependency2.doSomething()
    verify { dep2.doSomething() }
  }

  @Test fun testDynamicDependency() {
    val mxo = MockspressoBuilder()
      .dependencyOf { SomeObject(get(dependencyKey()), get(dependencyKey())) }
      .build()

    val dep1 by mxo.depOf { SomeDependency1() }
    val dep2 by mxo.depOf { SomeDependency2() }
    val someObject: SomeObject by mxo.findDep()

    assertThat(someObject.dependency1).isEqualTo(dep1)
    assertThat(someObject.dependency2).isEqualTo(dep2)
  }

  @Test fun testDynamicDependencyInProp() {
    val mxo = MockspressoBuilder().build()

    val someObject by mxo.depOf { SomeObject(get(dependencyKey()), get(dependencyKey())) }
    val dep1 by mxo.depOf { SomeDependency1() }
    val dep2 by mxo.depOf { SomeDependency2() }

    assertThat(someObject.dependency1).isEqualTo(dep1)
    assertThat(someObject.dependency2).isEqualTo(dep2)
  }

  @Test fun testFakeOf() {
    val mxo = MockspressoBuilder().build()

    val ro: SomeObjectWithIFaceDep by mxo.realInstance()
    val dep1 by mxo.depOf { SomeDependency1() }
    val dep2 by mxo.fakeOf<SomeDependency2Interface, SomeDependency2> { SomeDependency2() }

    dep2.doSomething() // method only exists on class
    assertThat(ro.dependency1).isEqualTo(dep1)
    assertThat(ro.dependency2).all {
      isEqualTo(dep2)
      isInstanceOf(SomeDependency2::class)
    }
  }

  private class SomeObject(val dependency1: SomeDependency1, val dependency2: SomeDependency2) {
    fun doSomething() {}
  }

  private class SomeObjectWithIFaceDep(val dependency1: SomeDependency1, val dependency2: SomeDependency2Interface)

  interface SomeDependency2Interface

  private class SomeDependency1
  private class SomeDependency2 : SomeDependency2Interface {
    fun doSomething() {}
  }

  private class SomeBadObjectWithSameDepMultipleTimes(
    val dependency1: SomeDependency1,
    val dependency2: SomeDependency1
  )
}
