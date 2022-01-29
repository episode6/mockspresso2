package com.episode6.mxo2.internal

import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.reflect.DependencyKey

private typealias Maker<T> = Dependencies.() -> T

internal class DependencyCacheBuilder {
  private val map: MutableMap<DependencyKey<*>, Maker<*>> = mutableMapOf()

  fun containsKey(key: DependencyKey<*>): Boolean = map.containsKey(key)

  fun <T : Any?> put(key: DependencyKey<T>, maker: Dependencies.() -> T) {
    map[key] = maker
  }

  @Suppress("UNCHECKED_CAST")
  fun build(makeDependencies: DependencyValidator.()->Dependencies): DependencyCache {
    val cache = DependencyCache()
    map.entries.forEach { (key, maker) ->
      key as DependencyKey<Any?>
      val validator = DependencyValidator(key)
      cache.put(key, validator) { validator.makeDependencies().maker() }
    }
    return cache
  }
}
