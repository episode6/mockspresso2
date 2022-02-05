@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE") // variables are intentionally unused

package com.episode6.mxo2.plugins.javax.inject

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isFailure
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.realInstance
import org.junit.jupiter.api.Test
import javax.inject.Inject

class BadInjectRealObjectMakerTest {

  @Test fun testNoConstructor() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: HasNoConstructor by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(NoInjectConstructorsException::class)
  }

  @Test fun testTooManyConstructors() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingJavaxInjectRules().build()
    val ro: HasTooManyConstructors by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(MultipleInjectConstructorsException::class)
  }

  class HasNoConstructor
  class HasTooManyConstructors @Inject constructor() {
    @Inject constructor(someVar: Int) : this()
  }
}
