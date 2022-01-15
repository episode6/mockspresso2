package com.episode6.mxo2.plugins.mockito

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.asKClass
import org.mockito.Mockito

/**
 * Use mockito to generate fallback objects for dependencies that are not present in the mockspresso instance
 */
@Suppress("UNCHECKED_CAST") fun MockspressoBuilder.fallbackWithMockito(): MockspressoBuilder =
  makeFallbackObjectsWith(object : FallbackObjectMaker {
    override fun <T> makeObject(key: DependencyKey<T>): T = Mockito.mock(key.token.asKClass().java as Class<T>)
  })
