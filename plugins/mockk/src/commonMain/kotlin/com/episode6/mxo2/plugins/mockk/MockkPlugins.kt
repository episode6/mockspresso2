@file:Suppress("REDUNDANT_SPREAD_OPERATOR_IN_NAMED_FORM_IN_FUNCTION") // mockk source code does this, when they stop, we'll stop

package com.episode6.mxo2.plugins.mockk

import com.episode6.mxo2.*
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.asKClass
import com.episode6.mxo2.reflect.dependencyKey
import com.episode6.mxo2.reflect.typeToken
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

/**
 * Add a mockk with the supplied params as a dependency in this mockspresso instance. Mock will be bound with the
 * supplied qualifier annotation. If you need a reference to the mock dependency, consider [MockspressoProperties.mockk]
 * instead.
 *
 * IMPORTANT: we default [relaxed] and [relaxUnitFun] to true for defaultMocks.
 */
inline fun <reified T : Any?> MockspressoBuilder.mockk(
  qualifier: Annotation? = null,
  name: String? = null,
  relaxed: Boolean = true,
  vararg moreInterfaces: KClass<*>,
  relaxUnitFun: Boolean = true,
  noinline block: T.() -> Unit = {}
) = dependency<T>(qualifier) {
  _mockk(
    name = name,
    relaxed = relaxed,
    moreInterfaces = *moreInterfaces,
    relaxUnitFun = relaxUnitFun,
    block = block
  )
}

/**
 * Add a mockk with the supplied params as a dependency in this mockspresso instance. Mockk will be bound with the
 * supplied qualifier annotation and will be accessible via the returned lazy.
 */
inline fun <reified T : Any?> MockspressoProperties.mockk(
  qualifier: Annotation? = null,
  name: String? = null,
  relaxed: Boolean = false,
  vararg moreInterfaces: KClass<*>,
  relaxUnitFun: Boolean = false,
  noinline block: T.() -> Unit = {}
): Lazy<T> = dependency<T>(qualifier) {
  _mockk(
    name = name,
    relaxed = relaxed,
    moreInterfaces = *moreInterfaces,
    relaxUnitFun = relaxUnitFun,
    block = block
  )
}

/**
 * Create a real object of [T] using mockspresso then wrap it in a [spyk]. This spy will be part of the mockspresso
 * graph and can be used by other real objects (and then verified in test code).
 */
inline fun <reified T : Any?> MockspressoProperties.spyk(
  qualifier: Annotation? = null,
  name: String? = null,
  vararg moreInterfaces: KClass<*>,
  recordPrivateCalls: Boolean = false,
  noinline block: T.() -> Unit = {}
): Lazy<T> = realImplementation(dependencyKey<T>(qualifier), typeToken<T>()) {
  _spyk(
    objToCopy = it!!,
    name = name,
    moreInterfaces = *moreInterfaces,
    recordPrivateCalls = recordPrivateCalls,
    block = block,
  )
}

/**
 * Create a real object of type [IMPL] using mockspresso then wrap it in a [spyk] (the object will be bound
 * using type [BIND]). This spy will be part of the mockspresso graph and can be used by other real objects
 * (and then verified in test code).
 */
inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoProperties.spykImplOf(
  qualifier: Annotation? = null,
  name: String? = null,
  vararg moreInterfaces: KClass<*>,
  recordPrivateCalls: Boolean = false,
  noinline block: IMPL.() -> Unit = {}
): Lazy<IMPL> = realImplementation(dependencyKey<BIND>(qualifier), typeToken<IMPL>()) {
  _spyk(
    objToCopy = it!!,
    name = name,
    moreInterfaces = *moreInterfaces,
    recordPrivateCalls = recordPrivateCalls,
    block = block,
  )
}
