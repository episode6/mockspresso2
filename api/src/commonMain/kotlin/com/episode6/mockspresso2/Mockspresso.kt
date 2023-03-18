package com.episode6.mockspresso2

import com.episode6.mockspresso2.api.Dependencies
import com.episode6.mockspresso2.api.DynamicObjectMaker
import com.episode6.mockspresso2.api.FallbackObjectMaker
import com.episode6.mockspresso2.api.RealObjectMaker
import com.episode6.mockspresso2.reflect.DependencyKey
import com.episode6.mockspresso2.reflect.TypeToken

/**
 * The main interface returned from a [MockspressoBuilder]. It implements [MockspressoInstance] but under the hood,
 * the instance it implements is lazily initiated and doesn't become ensured/immutable until its dependencies are
 * accessed.
 *
 * IMPORTANT: Calling any of the [MockspressoProperties] methods after the instance has already been ensured
 * will throw errors at runtime.
 */
interface Mockspresso : MockspressoInstance, MockspressoProperties {

  /**
   * Ensure the [MockspressoInstance] is initialized and all onSetup commands have been invoked
   */
  fun ensureInit()

  /**
   * Invokes all onTeardown commands for this and all parent [Mockspresso] instances
   */
  fun teardown()
}

/**
 * A fully-constructed instance of a Mockspresso dependency map.
 */
interface MockspressoInstance {

  /**
   * Create a new real object using the rules and dependencies in the mockspresso instance.
   *
   * Calling this method will ensure this [MockspressoInstance] is initialized.
   */
  fun <T : Any?> createNow(key: DependencyKey<T>): T

  /**
   * Find an existing dependency in this mockspresso instance. If the dependency hasn't been cached or constructed then
   * it will be generated on the fly and cached from that point forward. If the binding hasn't been declared in this
   * mockspresso instance, then a fallback will be generated.
   *
   * Calling this method will ensure this [MockspressoInstance] is initialized.
   */
  fun <T : Any?> findNow(key: DependencyKey<T>): T

  /**
   * Returns a new [MockspressoBuilder] using this Mockspresso instance as a parent.
   *
   * This method will NOT ensure this [MockspressoInstance] is initialized (i.e. it's possible to build new mockspresso
   * instances off of lazily instantiated ones, and the parents will be ensured when first accessed).
   */
  fun buildUpon(initBlock: MockspressoProperties.() -> Unit = {}): Mockspresso
}

/**
 * An interface that represents a [MockspressoInstance] that has not yet been fully constructed/ensured. It allows us
 * to make changes to the graph while also leveraging kotlin's delegated properties to grab lazy references from it
 * (that will be available after the [MockspressoInstance] under the hood has been ensured).
 *
 * Most of the methods in the interface should be used in conjunction with Kotlin's delegated properties
 * (aka "by") syntax.
 */
interface MockspressoProperties {

  /**
   * Add a callback that will fire when the [MockspressoInstance] is fully instantiated/ensured.
   */
  fun onSetup(cmd: (MockspressoInstance) -> Unit)

  /**
   * Add a callback that will fire when/if the [MockspressoInstance] is eventually torn down. (Automatic tear-down
   * is not supported by default but can be configured using plugins).
   */
  fun onTeardown(cmd: () -> Unit)

  /**
   * Define how this [MockspressoInstance] will construct real objects. By default, mockspresso will reflectively
   * call the primary constructor of a given class and pass appropriate dependencies to it.
   */
  fun makeRealObjectsWith(realMaker: RealObjectMaker)

  /**
   * Define how this [MockspressoInstance] will make fallback objects (i.e. dependencies that have not been explicitly
   * registered/cached within this instance). Usually this should be supplied by one of the mocking support plugins
   * (i.e. plugins-mockito or plugins-mockk).
   *
   * By default, mockspresso ships with a no-op [FallbackObjectMaker] that throws exceptions when called.
   */
  fun makeFallbackObjectsWith(fallbackMaker: FallbackObjectMaker)

  /**
   * Adds a [DynamicObjectMaker] to this [MockspressoInstance]. A [DynamicObjectMaker] gets a chance to supply any
   * un-cached/undefined dependency before the request goes to the [FallbackObjectMaker]. This enables mockspresso
   * plugins supply dependencies based on properties other than concrete types (i.e. generic types, class annotations,
   * etc.). It also allows for "default" instances for bindings, which can be overridden by an explicit dependency.
   */
  fun addDynamicObjectMaker(dynamicMaker: DynamicObjectMaker)

  /**
   * Register a dependency provided by [provider], bound in the mockspresso graph with [key] and return a [Lazy]
   * of that object.
   *
   * IMPORTANT: Reading the value from the returned lazy will cause the underlying [MockspressoInstance] to be ensured
   * if it hasn't been already.
   */
  fun <T : Any?> dependency(key: DependencyKey<T>, provider: Dependencies.() -> T): Lazy<T>

  /**
   * Find an existing dependency in the underlying mockspresso instance (bound with [key]) and return a [Lazy] for it.
   *
   * IMPORTANT: Reading the value from the returned lazy will cause the underlying [MockspressoInstance] to be ensured
   * if it hasn't been already.
   *
   * If the dependency hasn't been cached or constructed then it will be generated on the fly and cached from that
   * point forward. If the binding hasn't been declared in this mockspresso instance, then a fallback will be generated.
   */
  fun <T : Any?> findDependency(key: DependencyKey<T>): Lazy<T>

  /**
   * Register a request to create a real object of type [implementationToken] bound in the mockspresso graph with [key].
   * The supplied [interceptor] lambda will be called when the real object is created and allows the test code to wrap
   * the newly constructed real object before it's used. This enables the mock-support plugins to include spy support.
   *
   * Returns a [Lazy] of the resulting real object
   * IMPORTANT: Reading the value from the returned lazy will cause the underlying [MockspressoInstance] to be ensured
   * if it hasn't been already.
   */
  fun <BIND : Any?, IMPL : BIND> interceptRealImplementation(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> IMPL = { it }
  ): Lazy<IMPL>
}
