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

fun interface ObjectMaker {
  fun makeObject(key: DependencyKey<*>, dependencies: Dependencies): Any?
}

fun interface DynamicObjectMaker {
  sealed class Answer {
    data class Yes(val value: Any?) : Answer()
    object No : Answer()
  }

  fun canMakeObject(key: DependencyKey<*>, dependencies: Dependencies): Answer
}

interface FallbackObjectMaker {
  fun <T : Any?> makeObject(key: DependencyKey<T>): T
}
