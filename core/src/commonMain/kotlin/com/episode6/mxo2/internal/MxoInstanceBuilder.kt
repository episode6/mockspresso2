package com.episode6.mxo2.internal

import com.episode6.mxo2.DependencyAlreadyMappedError
import com.episode6.mxo2.MockspressoInstance
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.api.ObjectMaker
import com.episode6.mxo2.api.OptionalObjectMaker
import com.episode6.mxo2.defaultFallbackObjectMaker
import com.episode6.mxo2.defaultRealObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.TypeToken

internal class MxoInstanceBuilder(private val parent: Lazy<MxoInstance>? = null) {

  private var realMaker: ObjectMaker? = null
  private var fallbackMaker: FallbackObjectMaker? = null

  private val specialMakers: MutableList<OptionalObjectMaker> = mutableListOf()

  private val dependencies = DependencyCache()
  private val realObjectRequests = RealObjectRequestsList()

  private val setupCallbacks: MutableList<(MockspressoInstance) -> Unit> = mutableListOf()
  private val teardownCallbacks: MutableList<() -> Unit> = mutableListOf()

  fun onSetup(cmd: (MockspressoInstance) -> Unit) {
    setupCallbacks += cmd
  }

  fun onTearDown(cmd: () -> Unit) {
    teardownCallbacks += cmd
  }

  fun realObjectMaker(realMaker: ObjectMaker) {
    this.realMaker = realMaker
  }

  fun specialObjectMakers(vararg untypedMakers: OptionalObjectMaker) {
    specialMakers += untypedMakers
  }

  fun fallbackObjectMaker(fallbackMaker: FallbackObjectMaker) {
    this.fallbackMaker = fallbackMaker
  }

  fun <T : Any?> dependencyOf(key: DependencyKey<T>, provider: () -> T) {
    key.assertNotMapped()
    dependencies.put(key, DependencyValidator(key), provider)
  }

  fun <T : Any?> realObject(key: DependencyKey<T>, implementationToken: TypeToken<out T>) {
    key.assertNotMapped()
    realObjectRequests.put(key, implementationToken)
  }

  private fun DependencyKey<*>.assertNotMapped() {
    if (dependencies.containsKey(this) || realObjectRequests.containsKey(this)) {
      throw DependencyAlreadyMappedError(this)
    }
  }

  fun build() = parent?.value.let { parent ->
    MxoInstance(
      parent = parent,
      realMaker = realMaker ?: parent?.realMaker ?: defaultRealObjectMaker(),
      fallbackMaker = fallbackMaker ?: parent?.fallbackMaker ?: defaultFallbackObjectMaker(),
      specialMakers = specialMakers,
      setupCallbacks = setupCallbacks,
      teardownCallbacks = teardownCallbacks,
      dependencies = dependencies,
      realObjectRequests = realObjectRequests,
    )
  }
}

