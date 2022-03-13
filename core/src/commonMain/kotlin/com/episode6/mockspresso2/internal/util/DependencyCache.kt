package com.episode6.mockspresso2.internal.util

import com.episode6.mockspresso2.reflect.DependencyKey

// Stores lazies of dependencies in an immutable MxoInstance
internal class DependencyCache {
  private val map: MutableMap<DependencyKey<*>, CacheEntry> = mutableMapOf()

  fun containsKey(key: DependencyKey<*>): Boolean = map.containsKey(key)

  fun <T : Any?> put(key: DependencyKey<T>, validator: DependencyValidator, provider: () -> T) {
    map[key] = CacheEntry(mlazy(provider), validator)
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any?> get(key: DependencyKey<T>, validator: DependencyValidator): T = map[key]!!.let {
    it.validator.absorb(validator)
    it.lazy.value as T
  }
}

private class CacheEntry(val lazy: Lazy<*>, val validator: DependencyValidator)
