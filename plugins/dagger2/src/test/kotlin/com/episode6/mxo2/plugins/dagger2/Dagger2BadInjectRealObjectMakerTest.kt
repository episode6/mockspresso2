@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE") // variables are intentionally unused
package com.episode6.mxo2.plugins.dagger2

import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isFailure
import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.plugins.javax.inject.reflect.MultipleInjectConstructorsException
import com.episode6.mxo2.plugins.javax.inject.reflect.NoInjectConstructorsException
import com.episode6.mxo2.realInstance
import dagger.assisted.AssistedInject
import org.junit.jupiter.api.Test
import javax.inject.Inject

class Dagger2BadInjectRealObjectMakerTest {

  @Test fun testNoConstructor() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingDagger2Rules().build()
    val ro: HasNoConstructor by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(NoInjectConstructorsException::class)
  }

  @Test fun testTooManyConstructors1() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingDagger2Rules().build()
    val ro: HasTooManyConstructors1 by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(MultipleInjectConstructorsException::class)
  }

  @Test fun testTooManyConstructors2() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingDagger2Rules().build()
    val ro: HasTooManyConstructors2 by mxo.realInstance()

    assertThat { mxo.ensureInit() }.isFailure().hasClass(MultipleInjectConstructorsException::class)
  }

  @Test fun testTooManyConstructors3() {
    val mxo = MockspressoBuilder().makeRealObjectsUsingDagger2Rules().build()
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
