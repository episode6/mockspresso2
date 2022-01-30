package com.episode6.mxo2.plugins.mockk

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.asKClass
import io.mockk.MockK
import io.mockk.MockKGateway

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
