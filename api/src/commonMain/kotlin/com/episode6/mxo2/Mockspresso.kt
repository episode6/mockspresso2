package com.episode6.mxo2

import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.api.DynamicObjectMaker
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.api.ObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.TypeToken

/**
 * The main interface returned from a [MockspressoBuilder]. It implements [MockspressoInstance] but under the hood,
 * the instance it implements is lazily initiated and doesn't become ensured/immutable until its dependencies are
 * accessed.
 *
 * IMPORTANT: Calling any of the [MockspressoProperties] methods after the graph has already been ensured
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
  fun <T : Any?> createRealObject(key: DependencyKey<T>): T

  /**
   * Find an existing dependency in this mockspresso instance. If the dependency hasn't been cached or constructed then
   * it will be generated on the fly and cached from that point forward. If the binding hasn't been declared in this
   * mockspresso instance, then a fallback will be generated.
   *
   * Calling this method will ensure this [MockspressoInstance] is initialized.
   */
  fun <T : Any?> findDependency(key: DependencyKey<T>): T

  /**
   * Returns a new [MockspressoBuilder] using this Mockspresso instance as a parent.
   *
   * This method will NOT ensure this [MockspressoInstance] is initialized (i.e. it's possible to build new mockspresso
   * instances off of lazily instantiated ones, and the parents will be ensured when first accessed).
   */
  fun buildUpon(): MockspressoBuilder
}

/**
 * Builds a mockspresso [Mockspresso] instance that is lazily instantiated under the hood.
 */
interface MockspressoBuilder {
  fun onSetup(cmd: (MockspressoInstance) -> Unit): MockspressoBuilder
  fun onTeardown(cmd: () -> Unit): MockspressoBuilder

  fun makeRealObjectsWith(realMaker: ObjectMaker): MockspressoBuilder // formerly injector
  fun makeFallbackObjectsWith(fallbackMaker: FallbackObjectMaker): MockspressoBuilder // formerly mocker

  fun addDynamicObjectMaker(dynamicMaker: DynamicObjectMaker): MockspressoBuilder // formerly special object makers

  fun <T : Any?> addDependencyOf(key: DependencyKey<T>, provider: Dependencies.() -> T): MockspressoBuilder
  fun <BIND : Any?, IMPL : BIND> useRealImplOf(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> BIND = { it }
  ): MockspressoBuilder

  fun testResources(maker: (MockspressoProperties) -> Unit): MockspressoBuilder

  fun build(): Mockspresso
}

/**
 * An interface that represents a [MockspressoInstance] that has not yet been fully constructed/ensured. It allows us
 * to make changes to the graph while also leveraging kotlin's delegated properties to grab lazy references from it
 * (that will be available after the [MockspressoInstance] under the hood has been ensured).
 */
interface MockspressoProperties {
  fun onSetup(cmd: (MockspressoInstance) -> Unit)
  fun onTeardown(cmd: () -> Unit)
  fun <T : Any?> depOf(key: DependencyKey<T>, provider: Dependencies.() -> T): Lazy<T>
  fun <T : Any?> findDepOf(key: DependencyKey<T>): Lazy<T>
  fun <BIND : Any?, IMPL : BIND> realImplOf(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> IMPL = { it }
  ): Lazy<IMPL>
}
