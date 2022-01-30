package com.episode6.mxo2.plugins.mockk

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.asKClass
import io.mockk.MockKGateway

/**
 * Use mockk to generate fallback objects
 */
@Suppress("UNCHECKED_CAST")
fun MockspressoBuilder.fallbackWithMockk(
  relaxed: Boolean = true,
  relaxedUnitFun: Boolean = true,
): MockspressoBuilder = makeFallbackObjectsWith(object : FallbackObjectMaker {
  override fun <T> makeObject(key: DependencyKey<T>): T = MockKGateway.implementation().mockFactory.mockk(
    mockType = key.token.asKClass(),
    name = "automock:$key",
    relaxed = relaxed,
    moreInterfaces = emptyArray(),
    relaxUnitFun = relaxedUnitFun,
  ) as T
})
