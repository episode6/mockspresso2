@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE") // variables are intentionally unused
package com.episode6.mockspresso2.plugins.dagger2

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isFailure
import com.episode6.mockspresso2.Mockspresso
import com.episode6.mockspresso2.plugins.javax.inject.reflect.MultipleInjectConstructorsException
import com.episode6.mockspresso2.plugins.javax.inject.reflect.NoInjectConstructorsException
import com.episode6.mockspresso2.realInstance
import dagger.assisted.AssistedInject
import org.junit.jupiter.api.Test
import javax.inject.Inject

class Dagger2BadInjectRealObjectMakerTest {

  @Test fun testNoConstructor() {
    val mxo = Mockspresso { makeRealObjectsUsingDagger2Rules() }
    val ro: HasNoConstructor by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(NoInjectConstructorsException::class)
  }

  @Test fun testTooManyConstructors1() {
    val mxo = Mockspresso { makeRealObjectsUsingDagger2Rules() }
    val ro: HasTooManyConstructors1 by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(MultipleInjectConstructorsException::class)
  }

  @Test fun testTooManyConstructors2() {
    val mxo = Mockspresso { makeRealObjectsUsingDagger2Rules() }
    val ro: HasTooManyConstructors2 by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(MultipleInjectConstructorsException::class)
  }

  @Test fun testTooManyConstructors3() {
    val mxo = Mockspresso { makeRealObjectsUsingDagger2Rules() }
    val ro: HasTooManyConstructors3 by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(MultipleInjectConstructorsException::class)
  }

  class HasNoConstructor
  class HasTooManyConstructors1 @AssistedInject constructor() {
    @Inject constructor(someVar: Int) : this()
  }

  class HasTooManyConstructors2 @AssistedInject constructor() {
    @AssistedInject constructor(someVar: Int) : this()
  }

  class HasTooManyConstructors3 @Inject constructor() {
    @Inject constructor(someVar: Int) : this()
  }
}
