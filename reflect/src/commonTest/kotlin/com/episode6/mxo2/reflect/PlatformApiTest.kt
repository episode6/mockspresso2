package com.episode6.mxo2.reflect

import assertk.assertThat
import assertk.assertions.containsExactly
import kotlin.test.Test

class PlatformApiTest {

  @Test fun testSimpleGeneric() {
    val token = typeToken<GenericObj<String, Int, Float>>()

    val keys = token.asKClass().primaryConstructor().parameterKeys(token)

    assertThat(keys).containsExactly(
        dependencyKey<String>(),
        dependencyKey<Int>(),
        dependencyKey<Float>()
      )
  }

  @Test fun testGenericWithGeneric() {
    val token = typeToken<GenericWithGeneric<Float, Int>>()

    val keys = token.asKClass().primaryConstructor().parameterKeys(token)

    assertThat(keys).containsExactly(
      dependencyKey<GenericObj<Int, Float, Set<String>>>()
    )
  }

  @Test fun testRidiculousGenerics() {
    val token = typeToken<GenericWithGeneric<Map<String, Int>, List<Map<String, Int>>>>()

    val keys = token.asKClass().primaryConstructor().parameterKeys(token)

    assertThat(keys).containsExactly(
      dependencyKey<GenericObj<List<Map<String, Int>>, Map<String, Int>, Set<String>>>()
    )
  }

  private class GenericObj<A : Any, B : Any, C : Any>(val a: A, val b: B, val c: C)
  private class GenericWithGeneric<X : Any, Y : Any>(val child: GenericObj<Y, X, Set<String>>)
}
