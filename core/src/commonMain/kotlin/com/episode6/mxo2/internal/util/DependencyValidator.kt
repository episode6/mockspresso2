package com.episode6.mxo2.internal.util

import com.episode6.mxo2.CircularDependencyError
import com.episode6.mxo2.InternalMockspressoError
import com.episode6.mxo2.reflect.DependencyKey

// Validates that there are no circular dependencies involved in constructing a given object (to avoid
// stack-overflows and hangups in test-runs)
internal class DependencyValidator(
  private val key: DependencyKey<*>,
  private val parents: MutableSet<DependencyKey<*>> = mutableSetOf(),
  private val children: MutableSet<DependencyKey<*>> = mutableSetOf(),
) {

  fun absorb(other: DependencyValidator) {
    if (key != other.key) throw InternalMockspressoError("Tried to merge DependencyValidators with differing keys: $key, ${other.key}")
    if (parents matchesAnyOf other.children) throw CircularDependencyError(key)
    if (children matchesAnyOf other.parents) throw CircularDependencyError(key)
    parents += other.parents
    children += other.children
  }

  fun childValidator(childKey: DependencyKey<*>): DependencyValidator = DependencyValidator(
    key = validateNewChildKey(childKey),
    parents = (parents + key).toMutableSet()
  ).also { children += childKey }

  private fun validateNewChildKey(childKey: DependencyKey<*>): DependencyKey<*> {
    if (key == childKey || parents.contains(childKey)) throw CircularDependencyError(childKey)
    return childKey
  }
}

private infix fun Set<*>.matchesAnyOf(other: Set<*>): Boolean = any { other.contains(it) }
