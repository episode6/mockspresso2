package com.episode6.mockspresso2.internal

import com.episode6.mockspresso2.*
import com.episode6.mockspresso2.api.Dependencies
import com.episode6.mockspresso2.api.DynamicObjectMaker
import com.episode6.mockspresso2.api.FallbackObjectMaker
import com.episode6.mockspresso2.api.RealObjectMaker
import com.episode6.mockspresso2.internal.util.mlazy
import com.episode6.mockspresso2.reflect.DependencyKey
import com.episode6.mockspresso2.reflect.TypeToken

internal class MockspressoInstanceContainer(private val instance: MxoInstance) : MockspressoInstance {
  override fun <T> findNow(key: DependencyKey<T>): T = instance.get(key)
  override fun <T> createNow(key: DependencyKey<T>): T = instance.create(key)
  override fun buildUpon(): Mockspresso {
    return MockspressoContainer(properties = MockspressoPropertiesContainer(parent = mlazy { instance }))
  }
}

internal class MockspressoPropertiesContainer(parent: Lazy<MxoInstance>? = null) : MockspressoProperties {

  private var _builder: Lazy<MxoInstanceBuilder> = mlazy { MxoInstanceBuilder(parent) }
  private val builder: MxoInstanceBuilder get() = _builder.value
  val instanceLazy: MxoInstance by mlazy {
    val builder = builder
    _builder = mlazy { throw MockspressoAlreadyInitializedError() }
    return@mlazy builder.build()
  }

  override fun onSetup(cmd: (MockspressoInstance) -> Unit) {
    builder.onSetup(cmd)
  }

  override fun onTeardown(cmd: () -> Unit) {
    builder.onTearDown(cmd)
  }

  override fun makeRealObjectsWith(realMaker: RealObjectMaker) {
    builder.realObjectMaker(realMaker)
  }

  override fun makeFallbackObjectsWith(fallbackMaker: FallbackObjectMaker) {
    builder.fallbackObjectMaker(fallbackMaker)
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
    return mlazy { instanceLazy.get(key) as IMPL }
  }

  override fun <T> findDependency(key: DependencyKey<T>): Lazy<T> = mlazy { instanceLazy.get(key) }
}

internal class MockspressoContainer constructor(
  private val properties: MockspressoPropertiesContainer,
) : Mockspresso, MockspressoProperties by properties {
  private var instanceLazy = mlazy { properties.instanceLazy }
  private val instance get() = instanceLazy.value
  override fun ensureInit() = instance.ensureInit()
  override fun <T> findNow(key: DependencyKey<T>): T = instance.get(key)
  override fun <T> createNow(key: DependencyKey<T>): T = instance.create(key)

  override fun teardown() {
    instance.teardown()
    instanceLazy = mlazy { throw MockspressoAlreadyTornDownError() }
  }

  override fun buildUpon(): Mockspresso {
    return MockspressoContainer(properties = MockspressoPropertiesContainer(parent = instanceLazy))
  }
}
