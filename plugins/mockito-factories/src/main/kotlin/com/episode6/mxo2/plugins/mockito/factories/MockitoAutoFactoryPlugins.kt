package com.episode6.mxo2.plugins.mockito.factories

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.MockspressoProperties
import com.episode6.mxo2.addDependencyOf
import com.episode6.mxo2.api.DynamicObjectMaker
import com.episode6.mxo2.depOf
import com.episode6.mxo2.reflect.asKClass
import kotlin.reflect.full.hasAnnotation

/*
 * Mark type [T] (with optional [qualifier]) as a Factory object. The object will be mocked and each method will return
 * a dependency from the underlying Mockspresso instance.
 */
inline fun <reified T : Any?> MockspressoBuilder.autoFactory(qualifier: Annotation? = null): MockspressoBuilder =
  addDependencyOf<T>(qualifier) { autoFactoryMock<T>(qualifier) }

/**
 * Marks any class encountered with the given [A] annotation as a Factory object. The object will be mocked and each
 * method will return a dependency from the underlying Mockspresso instance.
 */
inline fun <reified A : Annotation> MockspressoBuilder.autoFactoriesByAnnotation(): MockspressoBuilder =
  addDynamicObjectMaker { key, deps ->
    when {
      key.token.asKClass().hasAnnotation<A>() -> DynamicObjectMaker.Answer.Yes(deps.autoFactoryMock(key.qualifier))
      else                                    -> DynamicObjectMaker.Answer.No
    }
  }

/**
 * Mark type [T] (with optional [qualifier]) as a Factory object which is also accessible via the returned lazy.
 * The object will be mocked and each method will return a dependency from the underlying Mockspresso instance.
 */
inline fun <reified T : Any?> MockspressoProperties.autoFactory(qualifier: Annotation? = null): Lazy<T> =
  depOf<T>(qualifier) { autoFactoryMock<T>(qualifier) }
