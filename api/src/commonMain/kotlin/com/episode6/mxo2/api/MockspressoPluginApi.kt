package com.episode6.mxo2.api

import com.episode6.mxo2.reflect.DependencyKey

/**
 * An accessor into the mockspresso dependency map that is passed to real and dynamic object makers which enables
 * them to inject dependencies into the objects they create.
 */
interface Dependencies {

  /**
   * Returns a dependency from mockspresso
   */
  fun <T : Any?> get(key: DependencyKey<T>): T
}

/**
 * A function that makes real objects (usually via reflection). Should always return a valid value or throw withh
 * a descriptive reason.
 */
fun interface ObjectMaker {
  /**
   * Return a new object matching the type represented in the [key]. Any dependencies needed can be pulled
   * from the [dependencies] object
   */
  fun makeObject(key: DependencyKey<*>, dependencies: Dependencies): Any?
}

/**
 * A function that gets a chance to create any unregistered dependency needed in a given test.
 */
fun interface DynamicObjectMaker {

  /**
   * Response type for [DynamicObjectMaker].
   */
  sealed interface Answer {

    /**
     * Answer to return when [DynamicObjectMaker] wants to create the object. The included [value] will be cached and
     * used for the provided dependencyKey
     */
    data class Yes(val value: Any?) : Answer

    /**
     * Answer to return when [DynamicObjectMaker] doesn't know how to create the given object.
     */
    object No : Answer
  }

  /**
   * Return an [Answer], if [Answer.Yes], the value must be castable as type represented in [key]
   */
  fun canMakeObject(key: DependencyKey<*>, dependencies: Dependencies): Answer
}

/**
 * A function that creates any dependencies needed by but not explicitly registered in a given test.
 * Usually implemented by returning mocks.
 */
interface FallbackObjectMaker {
  fun <T : Any?> makeObject(key: DependencyKey<T>): T
}
