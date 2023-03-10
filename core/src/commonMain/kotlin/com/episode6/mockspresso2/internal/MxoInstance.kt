package com.episode6.mockspresso2.internal

import com.episode6.mockspresso2.InvalidTypeReturnedFromMakerError
import com.episode6.mockspresso2.MockspressoInstance
import com.episode6.mockspresso2.api.Dependencies
import com.episode6.mockspresso2.api.DynamicObjectMaker
import com.episode6.mockspresso2.api.FallbackObjectMaker
import com.episode6.mockspresso2.api.RealObjectMaker
import com.episode6.mockspresso2.internal.util.DependencyCacheBuilder
import com.episode6.mockspresso2.internal.util.DependencyValidator
import com.episode6.mockspresso2.internal.util.RealObjectRequestsList
import com.episode6.mockspresso2.internal.util.runOnce
import com.episode6.mockspresso2.reflect.DependencyKey
import com.episode6.mockspresso2.reflect.asKClass

internal class MxoInstance(
  private val parent: MxoInstance? = null,
  val realMaker: RealObjectMaker,
  val fallbackMaker: FallbackObjectMaker,
  private val dynamicMakers: List<DynamicObjectMaker>,
  setupCallbacks: MutableList<(MockspressoInstance) -> Unit>,
  private val teardownCallbacks: List<() -> Unit>,
  dependenciesBuilder: DependencyCacheBuilder,
  private val realObjectRequests: RealObjectRequestsList,
) {

  private val dependencies = dependenciesBuilder.build { asDependencies() }

  val ensureInit: () -> Unit by runOnce {
    parent?.ensureInit?.invoke()

    // warm real objects cache
    realObjectRequests.forEach { key ->
      if (!dependencies.containsKey(key)) {
        createInternal(key, DependencyValidator(key), cache = true)
      }
    }

    dependencies.warmCache()
    
    // fire setup callbacks
    val container = MockspressoInstanceContainer(this)
    setupCallbacks.forEach { it.invoke(container) }
  }

  fun <T> get(key: DependencyKey<T>): T {
    ensureInit()
    return getInternal(key, DependencyValidator(key))
  }

  fun <T> create(key: DependencyKey<T>): T {
    ensureInit()
    return createInternal(key, DependencyValidator(key), cache = false)
  }

  fun teardown() {
    ensureInit()
    teardownCallbacks.forEach { it.invoke() }
    parent?.teardown()
  }

  private fun <T : Any?> canGetInternal(key: DependencyKey<T>, validator: DependencyValidator): TypedObjectAnswer<T> {
    if (dependencies.containsKey(key)) return Yes(dependencies.get(key, validator))
    if (realObjectRequests.containsKey(key)) return Yes(createInternal(key, validator, cache = true))

    val isSpecial = dynamicMakers.canMake(key, validator.asDependencies())
    if (isSpecial is DynamicObjectMaker.Answer.Yes) return isSpecial.castAndCache(key, validator)

    return parent?.canGetInternal(key, validator) ?: No
  }

  private fun <T : Any?> getInternal(key: DependencyKey<T>, validator: DependencyValidator): T {
    return when (val got = canGetInternal(key, validator)) {
      is Yes -> got.value
      is No  -> fallbackMaker.makeObject(key).cacheWith(key, validator)
    }
  }

  private fun <T : Any?> createInternal(key: DependencyKey<T>, validator: DependencyValidator, cache: Boolean): T =
    realMaker
      .makeRealObject(realObjectRequests.getImplFor(key), validator.asDependencies())
      .checkedCast(key)
      .let { realObjectRequests.intercept(key, it) }
      .also { if (cache) it.cacheWith(key, validator) }

  private fun DependencyValidator.asDependencies(): Dependencies = object : Dependencies {
    override fun <T> get(key: DependencyKey<T>): T = getInternal(key, childValidator(key))
  }

  private fun <T : Any?> T.cacheWith(key: DependencyKey<T>, validator: DependencyValidator): T =
    also { dependencies.put(key, validator) { it } }

  private fun <T : Any?> DynamicObjectMaker.Answer.Yes.castAndCache(
    key: DependencyKey<T>,
    validator: DependencyValidator
  ): Yes<T> =
    Yes(value.checkedCast(key).cacheWith(key, validator))
}

private fun List<DynamicObjectMaker>.canMake(
  key: DependencyKey<*>,
  dependencies: Dependencies
): DynamicObjectMaker.Answer = firstNotNullOfOrNull { maker ->
  maker.canMakeObject(key, dependencies).takeIf { it is DynamicObjectMaker.Answer.Yes }
} ?: DynamicObjectMaker.Answer.No

private sealed class TypedObjectAnswer<out T : Any?>
private data class Yes<T : Any?>(val value: T) : TypedObjectAnswer<T>()
private object No : TypedObjectAnswer<Nothing>()

@Suppress("UNCHECKED_CAST")
private fun <T : Any?> Any?.checkedCast(key: DependencyKey<T>): T {
  if (!key.token.asKClass().isInstance(this)) {
    throw InvalidTypeReturnedFromMakerError("Tried to create $key and got un-castable result: $this")
  }
  return this as T
}
