package com.episode6.mxo2.internal

import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.TypeToken

@Suppress("UNCHECKED_CAST")
internal class RealObjectRequestsList {
  private val map: MutableMap<DependencyKey<*>, RealObjectRequest> = mutableMapOf()
  fun containsKey(key: DependencyKey<*>): Boolean = map.containsKey(key)

  fun <BIND : Any?, IMPL : BIND> put(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> BIND
  ) {
    map[key] = RealObjectRequest(implementationToken, interceptor as (Any) -> Any)
  }

  fun <T : Any?> getImplFor(key: DependencyKey<T>): DependencyKey<out T> = DependencyKey(
    token = if (map.containsKey(key)) map[key]!!.implToken as TypeToken<out T> else key.token,
    qualifier = key.qualifier
  )

  fun <T : Any?> getInterceptorFor(key: DependencyKey<T>): (T) -> T {
    if (containsKey(key)) {
      return map[key]!!.interceptor as (T) -> T
    } else {
      return { it }
    }
  }

  fun forEach(consumer: (DependencyKey<*>) -> Unit) {
    map.keys.forEach(consumer)
  }
}

private data class RealObjectRequest(val implToken: TypeToken<*>, val interceptor: (Any) -> Any)
