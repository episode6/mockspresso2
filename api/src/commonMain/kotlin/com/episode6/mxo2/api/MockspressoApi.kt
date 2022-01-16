package com.episode6.mxo2.api

import com.episode6.mxo2.reflect.DependencyKey

interface Dependencies {
  fun <T : Any?> get(key: DependencyKey<T>): T
}

sealed class ObjectAnswer {
  data class Yes(val value: Any?) : ObjectAnswer()
  object No : ObjectAnswer()
}

fun interface ObjectMaker {
  fun makeObject(key: DependencyKey<*>, dependencies: Dependencies): Any?
}

fun interface DynamicObjectMaker {
  fun canMakeObject(key: DependencyKey<*>, dependencies: Dependencies): ObjectAnswer
}

interface FallbackObjectMaker {
  fun <T : Any?> makeObject(key: DependencyKey<T>): T
}
