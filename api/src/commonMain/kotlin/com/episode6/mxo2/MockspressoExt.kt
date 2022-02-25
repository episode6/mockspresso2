package com.episode6.mxo2

import com.episode6.mxo2.reflect.dependencyKey
import com.episode6.mxo2.reflect.typeToken

/**
 * Create a new real object of type [T] using the rules and dependencies in the mockspresso instance.
 *
 * Calling this method will ensure this [MockspressoInstance] is initialized.
 */
inline fun <reified T : Any?> MockspressoInstance.createNow(
  qualifier: Annotation? = null
): T = createNow(dependencyKey(qualifier))

/**
 * Find an existing dependency in this mockspresso instance of type [T] with the provided [qualifier]. If the
 * dependency hasn't been cached or constructed then it will be generated on the fly and cached from that point
 * forward. If the binding hasn't been declared in this mockspresso instance, then a fallback will be generated.
 *
 * Calling this method will ensure this [MockspressoInstance] is initialized.
 */
inline fun <reified T : Any?> MockspressoInstance.findNow(
  qualifier: Annotation? = null
): T = findNow(dependencyKey(qualifier))

/**
 * Register a dependency provided by [provider], bound in the mockspresso graph with a dependencyKey made from
 * type [T] and [qualifier].
 */
inline fun <reified T : Any?> MockspressoBuilder.dependency(
  qualifier: Annotation? = null,
  noinline provider: () -> T
): MockspressoBuilder = dependency(dependencyKey(qualifier), provider = { provider() })

/**
 * Register a request to create a real object of type [T] bound in the mockspresso graph with a dependencyKey made from
 * type [T] and [qualifier].
 *
 * The supplied [init] lambda will be called when the real object is created.
 */
inline fun <reified T : Any?> MockspressoBuilder.realInstance(
  qualifier: Annotation? = null,
  noinline init: (T) -> Unit = { }
): MockspressoBuilder = dependencyKey<T>(qualifier).let { key ->
  realImplementation(key, key.token, interceptor = { it.apply(init) })
}

/**
 * Register a request to create a real object of type [IMPL] bound in the mockspresso graph with a dependencyKey made
 * from type [BIND] and [qualifier].
 *
 * The supplied [init] lambda will be called when the real object is created.
 */
inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoBuilder.realImplementation(
  qualifier: Annotation? = null,
  noinline init: (IMPL) -> Unit = { }
): MockspressoBuilder =
  realImplementation(dependencyKey<BIND>(qualifier), typeToken<IMPL>(), interceptor = { it.apply(init) })

/**
 * Register a dependency provided by [provider], bound in the mockspresso graph with a dependencyKey made from
 * type [T] and [qualifier].
 *
 * Returns a [Lazy] with access to that dependency.
 *
 * IMPORTANT: Reading the value from the returned lazy will cause the underlying [MockspressoInstance] to be ensured
 * if it hasn't been already.
 */
inline fun <reified T : Any?> MockspressoProperties.dependency(
  qualifier: Annotation? = null,
  noinline provider: () -> T
): Lazy<T> = dependency(dependencyKey(qualifier), provider = { provider() })

/**
 * Register a dependency provided by [provider] that is of type [IMPL] but bound in the mockspresso graph with a
 * dependencyKey made from type [BIND] and [qualifier]. Returns a [Lazy] with access to that dependency as type [IMPL]
 *
 * IMPORTANT: Reading the value from the returned lazy will cause the underlying [MockspressoInstance] to be ensured
 * if it hasn't been already.
 */
@Suppress("UNCHECKED_CAST") inline fun <reified BIND : Any?, IMPL : BIND> MockspressoProperties.fake(
  qualifier: Annotation? = null,
  noinline provider: () -> IMPL
): Lazy<IMPL> {
  val depLazy = dependency<BIND>(qualifier, provider)
  return lazy(LazyThreadSafetyMode.NONE) { depLazy.value as IMPL }
}

/**
 * Find an existing dependency in the underlying mockspresso instance (bound with a dependencyKey of type
 * [T] + [qualifier]) and return a [Lazy] for access to it.
 *
 * IMPORTANT: Reading the value from the returned lazy will cause the underlying [MockspressoInstance] to be ensured
 * if it hasn't been already.
 *
 * If the dependency hasn't been cached or constructed then it will be generated on the fly and cached from that
 * point forward. If the binding hasn't been declared in this mockspresso instance, then a fallback will be generated.
 */
inline fun <reified T : Any?> MockspressoProperties.findDependency(
  qualifier: Annotation? = null
): Lazy<T> = findDependency(dependencyKey(qualifier))

/**
 * Register a request to create a real object of type [T] bound in the mockspresso graph with a dependencyKey made from
 * type [T] and [qualifier].
 *
 * The supplied [init] lambda will be called when the real object is created.
 *
 * Returns a [Lazy] of the resulting real object
 * IMPORTANT: Reading the value from the returned lazy will cause the underlying [MockspressoInstance] to be ensured
 * if it hasn't been already.
 */
inline fun <reified T : Any?> MockspressoProperties.realInstance(
  qualifier: Annotation? = null,
  noinline init: (T) -> Unit = { }
): Lazy<T> = dependencyKey<T>(qualifier).let { key ->
  realImplementation(key, key.token, interceptor = { it.apply(init) })
}

/**
 * Register a request to create a real object of type [IMPL] bound in the mockspresso graph with a dependencyKey made
 * from type [BIND] and [qualifier].
 *
 * The supplied [init] lambda will be called when the real object is created.
 *
 * Returns a [Lazy] of the resulting real object
 * IMPORTANT: Reading the value from the returned lazy will cause the underlying [MockspressoInstance] to be ensured
 * if it hasn't been already.
 */
inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoProperties.realImplementation(
  qualifier: Annotation? = null,
  noinline init: (IMPL) -> Unit = { }
): Lazy<IMPL> = realImplementation(dependencyKey<BIND>(qualifier), typeToken<IMPL>(), interceptor = { it.apply(init) })
