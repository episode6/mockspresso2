package com.episode6.mockspresso2

import com.episode6.mockspresso2.internal.MockspressoBuilderContainer

/**
 * Root entry point to create a new [MockspressoBuilder]
 */
fun MockspressoBuilder(): MockspressoBuilder = MockspressoBuilderContainer()
