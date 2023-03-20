package com.episode6.mockspresso2

import com.episode6.mockspresso2.internal.MockspressoBuilderContainer
import com.episode6.mockspresso2.internal.MockspressoContainer
import com.episode6.mockspresso2.internal.MockspressoPropertiesContainer


/**
 * Root entry point to create a new [Mockspresso] instance
 */
fun Mockspresso(): Mockspresso = MockspressoContainer(MockspressoPropertiesContainer())

/**
 * Root entry point to create a new [Mockspresso] instance with a convenience apply block
 */
fun Mockspresso(initBlock: MockspressoProperties.() -> Unit): Mockspresso = Mockspresso().apply(initBlock)

/**
 * Old entry point of mockspresso. Prefer the simplified [Mockspresso] method
 */
@Deprecated(message = "MockspressoBuilder is deprecated, prefer new syntax Mockspresso { /*builder method call */ }")
fun MockspressoBuilder(): MockspressoBuilder = MockspressoBuilderContainer(MockspressoPropertiesContainer())
