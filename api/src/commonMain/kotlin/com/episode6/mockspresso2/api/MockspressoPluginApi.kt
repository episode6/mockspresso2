package com.episode6.mockspresso2.api

import com.episode6.mockspresso2.reflect.DependencyKey

/**
 * An accessor into the mockspresso dependency map that is passed to real and dynamic object makers which enables
 * them to inject dependencies into the objects they create.
 */
public interface Dependencies {

  /**
   * Returns a dependency from mockspresso
   */
  public fun <T : Any?> get(key: DependencyKey<T>): T
}

/**
 * A function that makes real objects (usually via reflection). Should always return a valid value or throw withh
 * a descriptive reason.
 */
public fun interface RealObjectMaker {
  /**
   * Return a new object matching the type represented in the [key]. Any dependencies needed can be pulled
   * from the [dependencies] object
   */
  public fun makeRealObject(key: DependencyKey<*>, dependencies: Dependencies): Any?
}

/**
 * A function that gets a chance to create any unregistered dependency needed in a given test.
 */
public fun interface DynamicObjectMaker {

  /**
   * Response type for [DynamicObjectMaker].
   */
  public sealed interface Answer {

    /**
     * Answer to return when [DynamicObjectMaker] wants to create the object. The included [value] will be cached and
     * used for the provided dependencyKey
     */
    public data class Yes(val value: Any?) : Answer

    /**
     * Answer to return when [DynamicObjectMaker] doesn't know how to create the given object.
     */
    public object No : Answer
  }

  /**
   * Return an [Answer], if [Answer.Yes], the value must be castable as type represented in [key]
   */
  public fun canMakeObject(key: DependencyKey<*>, dependencies: Dependencies): Answer
}

/**
 * A function that creates any dependencies needed by but not explicitly registered in a given test.
 * Usually implemented by returning mocks.
 */
public interface FallbackObjectMaker {
  public fun <T : Any?> makeObject(key: DependencyKey<T>): T
}
