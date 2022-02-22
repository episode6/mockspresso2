package com.episode6.mxo2

import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.api.RealObjectMaker
import com.episode6.mxo2.plugins.core.reflect.reflectionRealObjectMaker
import com.episode6.mxo2.reflect.DependencyKey

/**
 * The default real object maker uses the default constructor to create objects
 */
fun defaultRealObjectMaker(): RealObjectMaker = reflectionRealObjectMaker()

/**
 * The default fallback object maker is non-functional and throws exceptions if any dependencies required by
 * real object are not explicitly added to the mockspresso instance.
 */
fun defaultFallbackObjectMaker(): FallbackObjectMaker = object : FallbackObjectMaker {
  override fun <T> makeObject(key: DependencyKey<T>): T = throw NoFallbackMakerProvidedError(key)
}
