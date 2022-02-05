package com.episode6.mxo2

import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.reflect.dependencyKey
import com.episode6.mxo2.reflect.typeToken

/**
 * @see [MockspressoInstance.createRealObject]
 */
inline fun <reified T : Any?> MockspressoInstance.createRealObject(
  qualifier: Annotation? = null
): T = createRealObject(dependencyKey(qualifier))

/**
 * @see [MockspressoInstance.findDependency]
 */
inline fun <reified T : Any?> MockspressoInstance.findDependency(
  qualifier: Annotation? = null
): T = findDependency(dependencyKey(qualifier))

/**
 * @see [MockspressoBuilder.addDependencyOf]
 */
inline fun <reified T : Any?> MockspressoBuilder.addDependencyOf(
  qualifier: Annotation? = null,
  noinline provider: Dependencies.() -> T
): MockspressoBuilder = addDependencyOf(dependencyKey(qualifier), provider)

/**
 * @see [MockspressoBuilder.useRealImplOf]
 *
 * Convenience method for use when creating a real object bound to its own type (i.e. BIND and IMPL are the same)
 */
inline fun <reified T : Any?> MockspressoBuilder.useRealInstanceOf(
  qualifier: Annotation? = null,
  noinline interceptor: (T) -> T = { it }
): MockspressoBuilder = dependencyKey<T>(qualifier).let { useRealImplOf(it, it.token, interceptor) }

/**
 * @see [MockspressoBuilder.useRealImplOf]
 */
inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoBuilder.useRealImplOf(
  qualifier: Annotation? = null,
  noinline interceptor: (IMPL) -> BIND = { it }
): MockspressoBuilder = useRealImplOf(dependencyKey<BIND>(qualifier), typeToken<IMPL>(), interceptor)

/**
 * @see [MockspressoProperties.depOf]
 */
inline fun <reified T : Any?> MockspressoProperties.depOf(
  qualifier: Annotation? = null,
  noinline provider: Dependencies.() -> T
): Lazy<T> = depOf(dependencyKey(qualifier), provider)

/**
 * @see [MockspressoProperties.findDepOf]
 */
inline fun <reified T : Any?> MockspressoProperties.findDep(
  qualifier: Annotation? = null
): Lazy<T> = findDepOf(dependencyKey(qualifier))

/**
 * @see [MockspressoProperties.realImplOf]
 *
 * Convenience method for use when creating a real object bound to its own type (i.e. BIND and IMPL are the same)
 */
inline fun <reified T : Any?> MockspressoProperties.realInstance(
  qualifier: Annotation? = null,
  noinline interceptor: (T) -> T = { it }
): Lazy<T> = dependencyKey<T>(qualifier).let { realImplOf(it, it.token, interceptor) }

/**
 * @see [MockspressoProperties.realImplOf]
 */
inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoProperties.realImplOf(
  qualifier: Annotation? = null,
  noinline interceptor: (IMPL) -> IMPL = { it }
): Lazy<IMPL> = realImplOf(dependencyKey<BIND>(qualifier), typeToken(), interceptor)
