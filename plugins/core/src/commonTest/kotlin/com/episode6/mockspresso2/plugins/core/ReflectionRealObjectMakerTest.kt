package com.episode6.mockspresso2.plugins.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.episode6.mockspresso2.api.Dependencies
import com.episode6.mockspresso2.api.RealObjectMaker
import com.episode6.mockspresso2.plugins.core.reflect.reflectionRealObjectMaker
import com.episode6.mockspresso2.reflect.allConstructors
import com.episode6.mockspresso2.reflect.asKClass
import com.episode6.mockspresso2.reflect.dependencyKey
import com.episode6.mockspresso2.reflect.parameterCount
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class ReflectionRealObjectMakerTest {

  val dependencies: Dependencies = mockk {
    every { get(dependencyKey<String>()) } returns "w00t"
    every { get(dependencyKey<Int>()) } returns 42
    every { get(dependencyKey<Long>()) } returns 420L
    every { get(dependencyKey<Boolean>()) } returns true
    every { get(dependencyKey<Float>()) } returns 4.2f
    every { get(dependencyKey<Double>()) } returns 42.1
  }

  @Test fun testDefault() {
    val maker = reflectionRealObjectMaker()

    val result = maker.test()

    with(result) {
      assertThat(constructorCall).isEqualTo("primary")
      assertThat(p1).isEqualTo("w00t")
      assertThat(p2).isEqualTo(42)
    }
    verify {
      with(dependencies) {
        get(dependencyKey<String>())
        get(dependencyKey<Int>())
      }
    }
  }

  @Test fun testLongest() {
    val maker =
      reflectionRealObjectMaker { token.asKClass().allConstructors().maxByOrNull { f -> f.parameterCount() }!! }

    val result = maker.test()

    with(result) {
      assertThat(constructorCall).isEqualTo("longest")
      assertThat(p1).isEqualTo("")
      assertThat(p2).isEqualTo(0)
    }
    verify {
      with(dependencies) {
        get(dependencyKey<Long>())
        get(dependencyKey<Boolean>())
        get(dependencyKey<Float>())
      }
    }
  }

  @Test fun testShortest() {
    val maker =
      reflectionRealObjectMaker { token.asKClass().allConstructors().minByOrNull { f -> f.parameterCount() }!! }

    val result = maker.test()

    with(result) {
      assertThat(constructorCall).isEqualTo("shortest")
      assertThat(p1).isEqualTo("")
      assertThat(p2).isEqualTo(0)
    }

    verify { dependencies.get(dependencyKey<Double>()) }
  }

  private fun RealObjectMaker.test(): TestClass = makeRealObject(dependencyKey<TestClass>(), dependencies)!! as TestClass
  @Suppress("unused", "UNUSED_PARAMETER") private class TestClass(val p1: String, val p2: Int) {
    var constructorCall = "primary"

    constructor(p3: Long, p4: Boolean, p5: Float) : this("", 0) {
      constructorCall = "longest"
    }

    constructor(p6: Double) : this("", 0) {
      constructorCall = "shortest"
    }
  }
}
