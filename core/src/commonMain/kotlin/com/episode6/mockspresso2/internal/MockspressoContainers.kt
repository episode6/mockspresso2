package com.episode6.mockspresso2.internal

import com.episode6.mockspresso2.*
import com.episode6.mockspresso2.api.Dependencies
import com.episode6.mockspresso2.api.DynamicObjectMaker
import com.episode6.mockspresso2.api.FallbackObjectMaker
import com.episode6.mockspresso2.api.RealObjectMaker
import com.episode6.mockspresso2.internal.util.mlazy
import com.episode6.mockspresso2.reflect.DependencyKey
import com.episode6.mockspresso2.reflect.TypeToken

internal class MockspressoBuilderContainer(parent: Lazy<MxoInstance>? = null) : MockspressoBuilder {

  private var _builder: Lazy<MxoInstanceBuilder> = mlazy { MxoInstanceBuilder(parent) }
  private val builder: MxoInstanceBuilder get() = _builder.value
  private val instanceLazy: Lazy<MxoInstance> = mlazy {
    val builder = builder
    _builder = mlazy { throw MockspressoAlreadyInitializedError() }
    return@mlazy builder.build()
  }

  private val properties = MockspressoPropertiesContainer(instanceLazy, getBuilder = { builder })

  override fun onSetup(cmd: (MockspressoInstance) -> Unit): MockspressoBuilder =
    apply { builder.onSetup(cmd) }

  override fun onTeardown(cmd: () -> Unit): MockspressoBuilder =
    apply { builder.onTearDown(cmd) }

  override fun makeRealObjectsWith(realMaker: RealObjectMaker): MockspressoBuilder =
    apply { builder.realObjectMaker(realMaker) }

  override fun addDynamicObjectMaker(dynamicMaker: DynamicObjectMaker): MockspressoBuilder =
    apply { builder.addDynamicMaker(dynamicMaker) }

  override fun makeFallbackObjectsWith(fallbackMaker: FallbackObjectMaker): MockspressoBuilder =
    apply { builder.fallbackObjectMaker(fallbackMaker) }

  override fun <T> dependency(key: DependencyKey<T>, provider: Dependencies.() -> T): MockspressoBuilder =
    apply { builder.dependencyOf(key, provider) }

  override fun <BIND : Any?, IMPL : BIND> interceptRealImplementation(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> BIND
  ): MockspressoBuilder = apply { builder.realObject(key, implementationToken, interceptor) }

  override fun testResources(maker: (MockspressoProperties) -> Unit): MockspressoBuilder =
    apply { maker(properties) }

  override fun build(): Mockspresso = MockspressoContainer(instanceLazy, properties)
}

internal class MockspressoInstanceContainer(private val instance: MxoInstance) : MockspressoInstance {
  override fun <T> findNow(key: DependencyKey<T>): T = instance.get(key)
  override fun <T> createNow(key: DependencyKey<T>): T = instance.create(key)
  override fun buildUpon(): MockspressoBuilder = MockspressoBuilderContainer(mlazy { instance })
}

private class MockspressoPropertiesContainer(
  instanceLazy: Lazy<MxoInstance>,
  private val getBuilder: () -> MxoInstanceBuilder,
) : MockspressoProperties {
  private val instance by instanceLazy
  private val builder get() = getBuilder()

  override fun onSetup(cmd: (MockspressoInstance) -> Unit) {
    builder.onSetup(cmd)
  }

  override fun onTeardown(cmd: () -> Unit) {
    builder.onTearDown(cmd)
  }

  override fun addDynamicObjectMaker(dynamicMaker: DynamicObjectMaker) {
    builder.addDynamicMaker(dynamicMaker)
  }

  override fun <T> dependency(key: DependencyKey<T>, provider: Dependencies.() -> T): Lazy<T> {
    builder.dependencyOf(key, provider)
    return findDependency(key)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <BIND, IMPL : BIND> interceptRealImplementation(
    key: DependencyKey<BIND>,
    implementationToken: TypeToken<IMPL>,
    interceptor: (IMPL) -> IMPL
  ): Lazy<IMPL> {
    builder.realObject(key, implementationToken, interceptor)
    return mlazy { instance.get(key) as IMPL }
  }

  override fun <T> findDependency(key: DependencyKey<T>): Lazy<T> = mlazy { instance.get(key) }
}

private class MockspressoContainer(
  private var instanceLazy: Lazy<MxoInstance>,
  properties: MockspressoProperties,
) : Mockspresso, MockspressoProperties by properties {
  private val instance get() = instanceLazy.value
  override fun ensureInit() = instance.ensureInit()
  override fun <T> findNow(key: DependencyKey<T>): T = instance.get(key)
  override fun <T> createNow(key: DependencyKey<T>): T = instance.create(key)

  override fun teardown() {
    instance.teardown()
    instanceLazy = mlazy { throw MockspressoAlreadyTornDownError() }
  }

  override fun buildUpon(): MockspressoBuilder = MockspressoBuilderContainer(instanceLazy)
}
