@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE") // variables are intentionally unused

package com.episode6.mockspresso2.plugins.javax.inject

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasClass
import com.episode6.mockspresso2.MockspressoBuilder
import com.episode6.mockspresso2.plugins.javax.inject.reflect.MultipleInjectConstructorsException
import com.episode6.mockspresso2.plugins.javax.inject.reflect.NoInjectConstructorsException
import com.episode6.mockspresso2.realInstance
import org.junit.jupiter.api.Test
import javax.inject.Inject

class BadInjectRealObjectMakerTest {

  @Test fun testNoConstructor() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: HasNoConstructor by mxo.realInstance()

    assertFailure { mxo.ensureInit() }.hasClass(NoInjectConstructorsException::class)
  }

  @Test fun testTooManyConstructors() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: HasTooManyConstructors by mxo.realInstance()

    assertFailure { mxo.ensureInit() }.hasClass(MultipleInjectConstructorsException::class)
  }

  class HasNoConstructor
  class HasTooManyConstructors @Inject constructor() {
    @Inject constructor(someVar: Int) : this()
  }
}
