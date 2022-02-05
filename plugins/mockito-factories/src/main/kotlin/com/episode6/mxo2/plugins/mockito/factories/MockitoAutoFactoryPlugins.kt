package com.episode6.mxo2.plugins.mockito.factories

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.MockspressoProperties
import com.episode6.mxo2.addDependencyOf
import com.episode6.mxo2.depOf

/*
 * Mark type [T] (with optional [qualifier]) as a Factory object. The object will be mocked and each method will return
 * a dependency from the underlying Mockspresso instance.
 */
inline fun <reified T : Any?> MockspressoBuilder.autoFactory(qualifier: Annotation? = null): MockspressoBuilder =
  addDependencyOf<T>(qualifier) { autoFactoryMock<T>(qualifier) }

/**
 * Mark type [T] (with optional [qualifier]) as a Factory object which is also accessible via the returned lazy.
 * The object will be mocked and each method will return a dependency from the underlying Mockspresso instance.
 */
inline fun <reified T : Any?> MockspressoProperties.autoFactory(qualifier: Annotation? = null): Lazy<T> =
  depOf<T>(qualifier) { autoFactoryMock<T>(qualifier) }
