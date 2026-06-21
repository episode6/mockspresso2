package com.episode6.mockspresso2

import com.episode6.mockspresso2.reflect.DependencyKey

public class MockspressoAlreadyInitializedError :
  AssertionError("Attempt to modify mockspresso graph after it's already been initialized")

public class MockspressoAlreadyTornDownError :
  AssertionError("Attempt to access mockspresso graph after it's already been torn down")

public class DependencyAlreadyMappedError(key: DependencyKey<*>) :
  AssertionError("Dependency has already been mapped: $key")

public class CircularDependencyError(key: DependencyKey<*>) :
  AssertionError("Circular dependency detected - key: $key)")

public class InvalidTypeReturnedFromMakerError(message: String) : AssertionError(message)

public class InternalMockspressoError(message: String) : AssertionError(message)

public class NoFallbackMakerProvidedError(key: DependencyKey<*>) : AssertionError("No FallbackObjectMaker provided; unable to generate binding for $key")
