package com.episode6.mxo2.internal.util

import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.TypeToken

// stores "requests" to create real objects until the MxoInstance is ensured
@Suppress("UNCHECKED_CAST")
internal class RealObjectRequestsList {
  private val map: MutableMap<DependencyKey<*>, RealObjectRequest> = mutableMapOf()
  fun containsKey(key: DependencyKey<*>): Boolean = map.containsKey(key)

  fun <BIND : Any?, IMPL : BIND> put(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> BIND
  ) {
    map[key] = RealObjectRequest(implementationToken, interceptor as (Any?) -> Any?)
  }

  fun <T : Any?> getImplFor(key: DependencyKey<T>): DependencyKey<out T> = DependencyKey(
    token = if (map.containsKey(key)) map[key]!!.implToken as TypeToken<out T> else key.token,
    qualifier = key.qualifier
  )

  // apply the interceptor lambda to the supplied [value] and return the result
  fun <T : Any?> intercept(key: DependencyKey<T>, value: T): T = when {
    containsKey(key) -> (map[key]!!.interceptor as (T) -> T).invoke(value)
    else             -> value
  }

  fun forEach(consumer: (DependencyKey<*>) -> Unit) {
    map.keys.forEach(consumer)
  }
}

private data class RealObjectRequest(val implToken: TypeToken<*>, val interceptor: (Any?) -> Any?)
