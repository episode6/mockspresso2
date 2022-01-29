package com.episode6.mxo2

import com.episode6.mxo2.api.DynamicObjectMaker
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.api.ObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.TypeToken

/**
 * The main interface returned from a [MockspressoBuilder]. Under the hood,
 * the [MockspressoInstance] it implements is lazily initiated and doesn't become
 * immutable until accessed.
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

interface MockspressoBuilder {
  fun onSetup(cmd: (MockspressoInstance) -> Unit): MockspressoBuilder
  fun onTeardown(cmd: () -> Unit): MockspressoBuilder

  fun makeRealObjectsWith(realMaker: ObjectMaker): MockspressoBuilder // formerly injector
  fun makeFallbackObjectsWith(fallbackMaker: FallbackObjectMaker): MockspressoBuilder // formerly mocker

  fun addDynamicObjectMaker(dynamicMaker: DynamicObjectMaker): MockspressoBuilder // formerly special object makers

  fun <T : Any?> addDependencyOf(key: DependencyKey<T>, provider: () -> T): MockspressoBuilder
  fun <BIND : Any?, IMPL : BIND> useRealImplOf(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> BIND = { it }
  ): MockspressoBuilder

  fun testResources(maker: (MockspressoProperties) -> Unit): MockspressoBuilder

  fun build(): Mockspresso
}

interface MockspressoInstance {
  fun <T : Any?> createRealObject(key: DependencyKey<T>): T
  fun <T : Any?> findDependency(key: DependencyKey<T>): T
  fun buildUpon(): MockspressoBuilder
}

interface MockspressoProperties {
  fun onSetup(cmd: (MockspressoInstance) -> Unit)
  fun onTeardown(cmd: () -> Unit)
  fun <T : Any?> depOf(key: DependencyKey<T>, maker: () -> T): Lazy<T>
  fun <T : Any?> findDepOf(key: DependencyKey<T>): Lazy<T>
  fun <BIND : Any?, IMPL : BIND> realImplOf(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> IMPL = { it }
  ): Lazy<IMPL>
}
