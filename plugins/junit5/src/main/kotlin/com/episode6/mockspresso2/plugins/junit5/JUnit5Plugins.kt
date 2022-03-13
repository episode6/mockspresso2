package com.episode6.mockspresso2.plugins.junit5

import com.episode6.mockspresso2.Mockspresso
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

interface MockspressoJUnit5Extension : Mockspresso, BeforeEachCallback, AfterEachCallback

/**
 * Create a junit extension to handle mockspresso lifecycle callbacks. Register the extension using the
 * [org.junit.jupiter.api.extension.RegisterExtension] annotation in junit5 tests.
 */
fun Mockspresso.junitExtension(): MockspressoJUnit5Extension =
  object : MockspressoJUnit5Extension, Mockspresso by this {
    override fun beforeEach(context: ExtensionContext) = ensureInit()
    override fun afterEach(context: ExtensionContext) = teardown()
  }
