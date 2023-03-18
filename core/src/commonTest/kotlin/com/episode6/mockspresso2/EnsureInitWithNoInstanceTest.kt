package com.episode6.mockspresso2

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class EnsureInitWithNoInstanceTest {
  private val mxo = Mockspresso()

  private var depCreated = false
  private val dep by mxo.dependency {
    depCreated = true
    Unit
  }

  @Test fun testNoInitNoTouch() {
    assertThat(depCreated).isFalse()
  }

  @Test fun testEnsureInit() {
    mxo.ensureInit()

    assertThat(depCreated).isTrue()
  }

  @Test fun testTouch() {
    dep

    assertThat(depCreated).isTrue()
  }
}
