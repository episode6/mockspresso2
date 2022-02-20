package com.episode6.mxo2

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isFailure
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class LifecycleTest {

  private val builderSetup: (MockspressoInstance) -> Unit = mockk(relaxed = true)
  private val propSetup: (MockspressoInstance) -> Unit = mockk(relaxed = true)
  private val builderTeardown: () -> Unit = mockk(relaxed = true)
  private val propTeardown: () -> Unit = mockk(relaxed = true)

  private val testDependency1: TestDependency1 = mockk(relaxUnitFun = true)
  private val testDependency2: TestDependency2 = mockk(relaxUnitFun = true)

  private val mxo = MockspressoBuilder()
    .onSetup(builderSetup)
    .onTeardown(builderTeardown)
    .dependencyOf { testDependency1 }
    .dependencyOf { testDependency2 }
    .build()

  init {
    mxo.onSetup(propSetup)
    mxo.onTeardown(propTeardown)
  }

  private val realObj1: TestRealObj1 by mxo.realInstance()
  @Suppress("unused") private val realObj2: TestRealObj2 by mxo.realInstance()

  @Test fun testNoSetup() {
    confirmAllVerified()
  }

  @Test fun testEnsureInit() {
    mxo.ensureInit()

    verify {
      builderSetup.invoke(any())
      propSetup.invoke(any())
      testDependency1.touch()
      testDependency2.touch()
    }
    confirmAllVerified()
  }

  @Test fun testTouchRealObj() {
    realObj1.touch()

    verify {
      builderSetup.invoke(any())
      propSetup.invoke(any())
      testDependency1.touch()
      testDependency2.touch()
    }
    confirmAllVerified()
  }

  @Test fun testEnsureInitThenTeardown() {
    mxo.ensureInit()
    mxo.teardown()

    verify {
      builderSetup.invoke(any())
      propSetup.invoke(any())
      testDependency1.touch()
      testDependency2.touch()

      builderTeardown.invoke()
      propTeardown.invoke()
    }
    confirmAllVerified()
  }

  @Test fun testTouchRealObjThenTeardown() {
    realObj1.touch()
    mxo.teardown()

    verify {
      builderSetup.invoke(any())
      propSetup.invoke(any())
      testDependency1.touch()
      testDependency2.touch()

      builderTeardown.invoke()
      propTeardown.invoke()
    }
    confirmAllVerified()
  }

  @Test fun testTeardownWithoutInit() {
    mxo.teardown()

    verify {
      builderSetup.invoke(any())
      propSetup.invoke(any())
      testDependency1.touch()
      testDependency2.touch()

      builderTeardown.invoke()
      propTeardown.invoke()
    }
    confirmAllVerified()
  }

  @Test fun testDoubleTeardown() {
    mxo.ensureInit()
    mxo.teardown()

    assertThat { mxo.teardown() }
      .isFailure()
      .hasClass(MockspressoAlreadyTornDownError::class)
  }

  private fun confirmAllVerified() {
    confirmVerified(builderSetup, builderTeardown, propSetup, propTeardown, testDependency1, testDependency2)
  }

  private interface TestDependency1 {
    fun touch()
  }
  private interface TestDependency2 {
    fun touch()
  }

  // test obj calls dep during init, which lets us confirm if it's been constructed or not
  private class TestRealObj1(val dep: TestDependency1) {
    init { dep.touch() }
    fun touch() {}
  }
  private class TestRealObj2(val dep: TestDependency2) {
    init { dep.touch() }
    fun touch() {}
  }
}
