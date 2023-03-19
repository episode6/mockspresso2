package com.episode6.mockspresso2.plugins.mockito.factories

import com.episode6.mockspresso2.MockspressoProperties
import com.episode6.mockspresso2.api.DynamicObjectMaker
import com.episode6.mockspresso2.plugins.mockito.factories.reflect.autoFactoryMock
import com.episode6.mockspresso2.reflect.asKClass
import com.episode6.mockspresso2.reflect.dependencyKey
import kotlin.reflect.full.hasAnnotation

/**
 * Marks any class encountered with the given [A] annotation as a Factory object. The object will be mocked and each
 * method will return a dependency from the underlying Mockspresso instance.
 */
inline fun <reified A : Annotation> MockspressoProperties.autoFactoriesByAnnotation() {
  addDynamicObjectMaker { key, deps ->
    when {
      key.token.asKClass().hasAnnotation<A>() -> DynamicObjectMaker.Answer.Yes(deps.autoFactoryMock(key))
      else                                    -> DynamicObjectMaker.Answer.No
    }
  }
}

/**
 * Mark type [T] (with optional [qualifier]) as a Factory object which is also accessible via the returned lazy.
 * The object will be mocked and each method will return a dependency from the underlying Mockspresso instance.
 */
inline fun <reified T : Any?> MockspressoProperties.autoFactory(qualifier: Annotation? = null): Lazy<T> {
  val key = dependencyKey<T>(qualifier)
  return dependency(key) { autoFactoryMock(key) }
}
