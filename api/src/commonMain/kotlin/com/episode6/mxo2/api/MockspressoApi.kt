package com.episode6.mxo2.api

import com.episode6.mxo2.reflect.DependencyKey

interface Dependencies {
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
