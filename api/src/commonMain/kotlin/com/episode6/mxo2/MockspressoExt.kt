package com.episode6.mxo2

import com.episode6.mxo2.reflect.dependencyKey
import com.episode6.mxo2.reflect.typeToken

inline fun <reified T : Any?> MockspressoBuilder.addDependencyOf(qualifier: Annotation? = null, noinline provider: () -> T): MockspressoBuilder =
  addDependencyOf(dependencyKey(qualifier), provider)

inline fun <reified T : Any?> MockspressoBuilder.useRealInstanceOf(qualifier: Annotation? = null): MockspressoBuilder =
  dependencyKey<T>(qualifier).let { useRealImplOf(it, it.token) }

inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoBuilder.useRealImplOf(qualifier: Annotation? = null): MockspressoBuilder =
  useRealImplOf(dependencyKey<BIND>(qualifier), typeToken<IMPL>())



inline fun <reified T : Any?> MockspressoInstance.createRealObject(qualifier: Annotation? = null): T =
  createRealObject(dependencyKey(qualifier))

inline fun <reified T : Any?> MockspressoInstance.findDependency(qualifier: Annotation? = null): T =
  findDependency(dependencyKey(qualifier))



inline fun <reified T : Any?> MockspressoProperties.depOf(qualifier: Annotation? = null, noinline provider: () -> T): Lazy<T> =
  depOf(dependencyKey(qualifier), provider)

inline fun <reified T : Any?> MockspressoProperties.findDep(qualifier: Annotation? = null): Lazy<T> =
  findDepOf(dependencyKey(qualifier))

inline fun <reified T : Any?> MockspressoProperties.realInstance(qualifier: Annotation? = null): Lazy<T> =
  dependencyKey<T>(qualifier).let { realImplOf(it, it.token) }

inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoProperties.realImplOf(qualifier: Annotation? = null): Lazy<IMPL> =
  realImplOf(dependencyKey<BIND>(qualifier), typeToken())
