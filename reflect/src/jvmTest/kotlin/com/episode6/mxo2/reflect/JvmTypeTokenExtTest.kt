package com.episode6.mxo2.reflect

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

class JvmTypeTokenExtTest {

  @Test fun testFirstLevel() {
    val token = typeToken<IFace<String, Int>>()
    val giveA = token.asKClass().java.getMethod("giveA")
    val giveB = token.asKClass().java.getMethod("giveB")
    val giveListA = token.asKClass().java.getMethod("giveListA")

    val giveAToken = token.resolveReturnTypeFromMethod(giveA)
    val giveBToken = token.resolveReturnTypeFromMethod(giveB)
    val giveListAToken = token.resolveReturnTypeFromMethod(giveListA)

    assertThat(giveAToken).isEqualTo(typeToken<String>())
    assertThat(giveBToken).isEqualTo(typeToken<Int>())
    assertThat(giveListAToken).isEqualTo(typeToken<List<String>>())
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

    assertThat(giveAToken).isEqualTo(typeToken<Int>())
    assertThat(giveBToken).isEqualTo(typeToken<String>())
    assertThat(giveListAToken).isEqualTo(typeToken<List<Int>>())
    assertThat(giveMapXYToken).isEqualTo(typeToken<Map<String, Int>>())
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
