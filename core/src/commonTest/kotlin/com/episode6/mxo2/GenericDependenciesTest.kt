package com.episode6.mxo2

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import kotlin.test.Test

class GenericDependenciesTest {

  @Test fun testSimpleGeneric() {
    val mxo = MockspressoBuilder()
      .addDependencyOf { 5 }
      .addDependencyOf { "hello" }
      .addDependencyOf { 4.2f }
      .build()

    val obj = mxo.createRealObject<GenericObj<String, Int, Float>>()

    assertThat(obj.a).isEqualTo("hello")
    assertThat(obj.b).isEqualTo(5)
    assertThat(obj.c).isEqualTo(4.2f)
  }

  @Test fun testGenericWithGeneric() {
    val mxo = MockspressoBuilder()
      .useRealInstanceOf<GenericObj<Int, Float, Set<String>>>()
      .addDependencyOf { 5 }
      .addDependencyOf { setOf("str1", "str2") }
      .addDependencyOf { 4.2f }
      .build()

    val obj = mxo.createRealObject<GenericWithGeneric<Float, Int>>()

    with(obj.child) {
      assertThat(a).isEqualTo(5)
      assertThat(b).isEqualTo(4.2f)
      assertThat(c).containsAll("str1", "str2")
    }
  }

  @Test fun testRidiculousGenerics() {
    val mxo = MockspressoBuilder()
      .useRealInstanceOf<GenericObj<List<Map<String, Int>>, Map<String, Int>, Set<String>>>()
      .useRealInstanceOf<GenericWithGeneric<Map<String, Int>, List<Map<String, Int>>>>()
      .addDependencyOf { setOf("setStr1", "setStr2") }
      .addDependencyOf { mapOf("str1" to 1) }
      .addDependencyOf {
        listOf(
          mapOf("str2" to 2),
          mapOf("str3" to 3)
        )
      }
      .build()

    val obj = mxo.createRealObject<ConcreteWithGenerics>()

    with(obj.child.child) {
      assertThat(a).isEqualTo(listOf(mapOf("str2" to 2), mapOf("str3" to 3)))
      assertThat(b).isEqualTo(mapOf("str1" to 1))
      assertThat(c).isEqualTo(setOf("setStr1", "setStr2"))
    }
  }

  private class GenericObj<A : Any, B : Any, C : Any>(val a: A, val b: B, val c: C)
  private class GenericWithGeneric<X : Any, Y : Any>(val child: GenericObj<Y, X, Set<String>>)
  private class ConcreteWithGenerics(val child: GenericWithGeneric<Map<String, Int>, List<Map<String, Int>>>)
}
