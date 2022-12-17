package com.episode6.mockspresso2

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.episode6.mockspresso2.api.DynamicObjectMaker
import kotlin.test.Test

class DynamicObjectMakerTest {

  @Test fun testUsageInBuilder() {
    val mxo = MockspressoBuilder()
      .addDynamicObjectMaker(defaultTestInterfaceMaker())
      .build()

    val parent: TestParent by mxo.realImplementation()

    assertThat(parent.testObject.id).isEqualTo("default")
  }

  @Test fun testUsageInProperties() {
    val mxo = MockspressoBuilder().build()
    mxo.addDynamicObjectMaker(defaultTestInterfaceMaker())
    val parent: TestParent by mxo.realImplementation()

    assertThat(parent.testObject.id).isEqualTo("default")
  }

  private fun defaultTestInterfaceMaker() = DynamicObjectMaker { key, _ ->
    when (key.token.type.classifier) {
      TestInterface::class -> DynamicObjectMaker.Answer.Yes(TestObject("default"))
      else                 -> DynamicObjectMaker.Answer.No
    }
  }

  private interface TestInterface {
    val id: String?
  }

  private class TestObject(override val id: String?) : TestInterface

  private class TestParent(val testObject: TestInterface)
}
