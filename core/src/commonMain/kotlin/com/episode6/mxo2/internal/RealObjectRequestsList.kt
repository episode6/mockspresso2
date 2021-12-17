package com.episode6.mxo2.internal

import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.TypeToken

internal class RealObjectRequestsList {
  private val map: MutableMap<DependencyKey<*>, TypeToken<*>> = mutableMapOf()
  fun containsKey(key: DependencyKey<*>): Boolean = map.containsKey(key)

  fun <T : Any?> put(key: DependencyKey<T>, implementationToken: TypeToken<out T>) {
    map[key] = implementationToken
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any?> getImplFor(key: DependencyKey<T>): DependencyKey<out T> = DependencyKey(
    token = if (map.containsKey(key)) map[key]!! as TypeToken<out T> else key.token,
    qualifier = key.qualifier
  )

  fun forEach(consumer: (DependencyKey<*>) -> Unit) {
    map.keys.forEach(consumer)
  }
}
