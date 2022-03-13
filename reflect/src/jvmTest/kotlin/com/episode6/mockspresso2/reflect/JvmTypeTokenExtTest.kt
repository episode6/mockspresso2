package com.episode6.mockspresso2.reflect

import assertk.assertThat
import assertk.assertions.isDataClassEqualTo
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

// tests whether we can successfully resolve generics from java into [TypeToken]s with KTypes
@Suppress("unused") // we're testing reflection here
class JvmTypeTokenExtTest {

  @Test fun testFirstLevel() {
    val token = typeToken<IFace<String, Int>>()
    val giveA = token.asKClass().java.getMethod("giveA")
    val giveB = token.asKClass().java.getMethod("giveB")
    val giveListA = token.asKClass().java.getMethod("giveListA")

    val giveAToken = token.resolveReturnTypeFromMethod(giveA)
    val giveBToken = token.resolveReturnTypeFromMethod(giveB)
    val giveListAToken = token.resolveReturnTypeFromMethod(giveListA)

    assertThat(giveAToken).isDataClassEqualTo(typeToken<String>())
    assertThat(giveBToken).isDataClassEqualTo(typeToken<Int>())
    assertThat(giveListAToken).isDataClassEqualTo(typeToken<List<String>>())
  }

  @Test fun testSecondLevel() {
    val token = typeToken<IFace2<String, Int>>()
    val giveA = token.asKClass().java.getMethod("giveA")
    val giveB = token.asKClass().java.getMethod("giveB")
    val giveListA = token.asKClass().java.getMethod("giveListA")
    val giveMapXY = token.asKClass().java.getMethod("giveMapXY")

    val giveAToken = token.resolveReturnTypeFromMethod(giveA)
    val giveBToken = token.resolveReturnTypeFromMethod(giveB)
    val giveListAToken = token.resolveReturnTypeFromMethod(giveListA)
    val giveMapXYToken = token.resolveReturnTypeFromMethod(giveMapXY)

    assertThat(giveAToken).isDataClassEqualTo(typeToken<Int>())
    assertThat(giveBToken).isDataClassEqualTo(typeToken<String>())
    assertThat(giveListAToken).isDataClassEqualTo(typeToken<List<Int>>())
    assertThat(giveMapXYToken).isDataClassEqualTo(typeToken<Map<String, Int>>())
  }

  @Test fun testThirdLevel() {
    val token = typeToken<ConcreteDef>()
    val giveA = token.asKClass().java.getMethod("giveA")
    val giveB = token.asKClass().java.getMethod("giveB")
    val giveListA = token.asKClass().java.getMethod("giveListA")
    val giveMapXY = token.asKClass().java.getMethod("giveMapXY")

    val giveAToken = token.resolveReturnTypeFromMethod(giveA)
    val giveBToken = token.resolveReturnTypeFromMethod(giveB)
    val giveListAToken = token.resolveReturnTypeFromMethod(giveListA)
    val giveMapXYToken = token.resolveReturnTypeFromMethod(giveMapXY)

    assertThat(giveAToken).isDataClassEqualTo(typeToken<Int>())
    assertThat(giveBToken).isDataClassEqualTo(typeToken<String>())
    assertThat(giveListAToken).isDataClassEqualTo(typeToken<List<Int>>())
    assertThat(giveMapXYToken).isDataClassEqualTo(typeToken<Map<String, Int>>())
  }

  @Test fun testFirstLevelReversed() {
    val token = typeToken<IFaceReverse<String>>()
    val giveA = token.asKClass().java.getMethod("giveA")
    val giveB = token.asKClass().java.getMethod("giveB")
    val giveListA = token.asKClass().java.getMethod("giveListA")

    val giveAToken = token.resolveReturnTypeFromMethod(giveA)
    val giveBToken = token.resolveReturnTypeFromMethod(giveB)
    val giveListAToken = token.resolveReturnTypeFromMethod(giveListA)

    assertThat(giveAToken).isDataClassEqualTo(typeToken<Long>())
    assertThat(giveBToken).isDataClassEqualTo(typeToken<String>())
    assertThat(giveListAToken).isDataClassEqualTo(typeToken<List<Long>>())
  }

  private interface IFace<A, B> {
    fun giveA(): A
    fun giveB(): B
    fun giveListA(): List<A>
  }

  private interface IFace2<X, Y> : IFace<Y, X> {
    fun giveMapXY(): Map<X, Y>
  }

  private interface IFaceReverse<A> : IFace<Long, A>

  private interface ConcreteDef : IFace2<String, Int>
}

private fun TypeToken<*>.resolveReturnTypeFromMethod(method: Method): TypeToken<*> =
  resolveJvmType(method.genericReturnType, method.declaringClass)
