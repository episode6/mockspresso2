package com.episode6.mxo2

import com.episode6.mxo2.reflect.DependencyKey

class MockspressoAlreadyInitializedError :
  AssertionError("Attempt to modify mockspresso graph after it's already been initialized")

class MockspressoAlreadyTornDownError :
  AssertionError("Attempt to access mockspresso graph after it's already been torn down")

class DependencyAlreadyMappedError(key: DependencyKey<*>) :
  AssertionError("Dependency has already been mapped: $key")

class CircularDependencyError(key: DependencyKey<*>) :
  AssertionError("Circular dependency detected - key: $key)")

class InvalidTypeReturnedFromMakerError(message: String) : AssertionError(message)

class InternalMockspressoError(message: String) : AssertionError(message)

class NoFallbackMakerProvidedError(key: DependencyKey<*>) : AssertionError("No FallbackObjectMaker provided; unable to generate binding for $key")
