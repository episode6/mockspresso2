package com.episode6.mxo2.plugins.mockk

import com.episode6.mxo2.*
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.asKClass
import io.mockk.MockK
import io.mockk.MockKGateway
import kotlin.reflect.KClass
import io.mockk.mockk as _mockk
import io.mockk.spyk as _spyk

/**
 * Use mockk to generate fallback objects for dependencies that are not present in the mockspresso instance
 */
@Suppress("UNCHECKED_CAST")
fun MockspressoBuilder.fallbackWithMockk(
  relaxed: Boolean = true,
  relaxedUnitFun: Boolean = true,
): MockspressoBuilder = makeFallbackObjectsWith(object : FallbackObjectMaker {
  // we're duplicating internal mockk code here to support creating generic mocks based on a [DependencyKey]
  override fun <T> makeObject(key: DependencyKey<T>): T = MockK.useImpl {
    MockKGateway.implementation().mockFactory.mockk(
      mockType = key.token.asKClass(),
      name = "automock:$key",
      relaxed = relaxed,
      moreInterfaces = emptyArray(),
      relaxUnitFun = relaxedUnitFun,
    ) as T
  }
})


inline fun <reified T : Any?> MockspressoBuilder.defaultMockk(
  qualifier: Annotation? = null,
  name: String? = null,
  relaxed: Boolean = true,
  vararg moreInterfaces: KClass<*>,
  relaxUnitFun: Boolean = true,
  noinline block: T.() -> Unit = {}
) = addDependencyOf<T>(qualifier) {
  _mockk(
    name = name,
    relaxed = relaxed,
    moreInterfaces = *moreInterfaces,
    relaxUnitFun = relaxUnitFun,
    block = block
  )
}

inline fun <reified T : Any?> MockspressoProperties.mockk(
  qualifier: Annotation? = null,
  name: String? = null,
  relaxed: Boolean = false,
  vararg moreInterfaces: KClass<*>,
  relaxUnitFun: Boolean = false,
  noinline block: T.() -> Unit = {}
): Lazy<T> = depOf<T>(qualifier) {
  _mockk(
    name = name,
    relaxed = relaxed,
    moreInterfaces = *moreInterfaces,
    relaxUnitFun = relaxUnitFun,
    block = block
  )
}

inline fun <reified T : Any?> MockspressoProperties.spyk(
  qualifier: Annotation? = null,
  name: String? = null,
  vararg moreInterfaces: KClass<*>,
  recordPrivateCalls: Boolean = false,
  noinline block: T.() -> Unit = {}
): Lazy<T> = realInstance<T>(qualifier) {
  _spyk(
    objToCopy = it!!,
    name = name,
    moreInterfaces = *moreInterfaces,
    recordPrivateCalls = recordPrivateCalls,
    block = block,
  )
}

inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoProperties.spykImplOf(
  qualifier: Annotation? = null,
  name: String? = null,
  vararg moreInterfaces: KClass<*>,
  recordPrivateCalls: Boolean = false,
  noinline block: IMPL.() -> Unit = {}
): Lazy<IMPL> = realImplOf<BIND, IMPL>(qualifier) {
  _spyk(
    objToCopy = it!!,
    name = name,
    moreInterfaces = *moreInterfaces,
    recordPrivateCalls = recordPrivateCalls,
    block = block,
  )
}
