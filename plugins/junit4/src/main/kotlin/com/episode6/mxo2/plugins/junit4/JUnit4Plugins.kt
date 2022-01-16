package com.episode6.mxo2.plugins.junit4

import com.episode6.mxo2.Mockspresso
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

interface MockspressoJUnit4Rule : Mockspresso, TestRule

/**
 * Create a junit rule to handle mockspresso lifecycle callbacks. Apply this using the [org.junit.Rule]
 * annotation in your test.
 */
fun Mockspresso.junitRule(): MockspressoJUnit4Rule = object : MockspressoJUnit4Rule, Mockspresso by this {
  override fun apply(base: Statement, description: Description): Statement = object : Statement() {
    override fun evaluate() {
      ensureInit()
      base.evaluate()
      teardown()
    }
  }
}
