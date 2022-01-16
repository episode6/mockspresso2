package com.episode6.mxo2.plugins.mockito

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.reflect.dependencyKey
import org.junit.jupiter.api.Test
import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub

class MockitoAutoFactoryTest {

  private val deps: Dependencies = mock()

  private inline fun <reified T : Any> makeMock(): T = deps.autoFactoryMock()

  @Test fun testFirstLevel() {
    deps.stub {
      onGetKey<String>() doReturn "string"
      onGetKey<Int>() doReturn 2
      onGetKey<List<String>>() doReturn listOf("strings")
    }
    val mock: IFace<String, Int> = makeMock()

    val a: String = mock.giveA()
    val b: Int = mock.giveB()
    val listA: List<String> = mock.giveListA()

    assertThat(a).isEqualTo("string")
    assertThat(b).isEqualTo(2)
    assertThat(listA).containsExactly("strings")
  }

  @Test fun testSecondLevel() {
    deps.stub {
      onGetKey<String>() doReturn "string"
      onGetKey<Int>() doReturn 2
      onGetKey<List<Int>>() doReturn listOf(3)
      onGetKey<Map<String, Int>>() doReturn mapOf("key" to 6)
    }
    val mock: IFace2<String, Int> = makeMock()

    val a: Int = mock.giveA()
    val b: String = mock.giveB()
    val listA: List<Int> = mock.giveListA()
    val mapXY: Map<String, Int> = mock.giveMapXY()

    assertThat(a).isEqualTo(2)
    assertThat(b).isEqualTo("string")
    assertThat(listA).containsExactly(3)
    assertThat(mapXY).containsOnly("key" to 6)
  }

  @Test fun testThirdLevel() {
    deps.stub {
      onGetKey<String>() doReturn "string"
      onGetKey<Int>() doReturn 2
      onGetKey<List<Int>>() doReturn listOf(3)
      onGetKey<Map<String, Int>>() doReturn mapOf("key" to 6)
    }
    val mock: ConcreteDef = makeMock()

    val a: Int = mock.giveA()
    val b: String = mock.giveB()
    val listA: List<Int> = mock.giveListA()
    val mapXY: Map<String, Int> = mock.giveMapXY()

    assertThat(a).isEqualTo(2)
    assertThat(b).isEqualTo("string")
    assertThat(listA).containsExactly(3)
    assertThat(mapXY).containsOnly("key" to 6)
  }

  @Test fun testFirstLevelReversed() {
    deps.stub {
      onGetKey<String>() doReturn "string"
      onGetKey<Long>() doReturn 2L
      onGetKey<List<Long>>() doReturn listOf(4L, 5L)
    }
    val mock: IFaceReverse<String> = makeMock()

    val a: Long = mock.giveA()
    val b: String = mock.giveB()
    val listA: List<Long> = mock.giveListA()

    assertThat(a).isEqualTo(2L)
    assertThat(b).isEqualTo("string")
    assertThat(listA).containsExactly(4L, 5L)
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

private inline fun <reified T : Any> KStubbing<Dependencies>.onGetKey() = onGeneric { get(dependencyKey<T>()) }
