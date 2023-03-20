package com.episode6.mockspresso2.plugins.junit4

import com.episode6.mockspresso2.Mockspresso
import com.episode6.mockspresso2.MockspressoInstance
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement

class Junit4PluginsTest {

  private val setupCmd: (MockspressoInstance) -> Unit = mockk(relaxed = true)
  private val teardownCmd: () -> Unit = mockk(relaxed = true)
  private val mxoRule = Mockspresso {
    onSetup(setupCmd)
    onTeardown(teardownCmd)
  }.junitRule()
  private val baseStatement: Statement = mockk(relaxUnitFun = true)
  private val description: Description = mockk()

  @Test fun verifyApplyCausesNoAction() {
    mxoRule.apply(baseStatement, description)

    confirmVerified(setupCmd, teardownCmd, baseStatement)
  }

  @Test fun verifyOrderOfCalls() {
    val wrappedStatement = mxoRule.apply(baseStatement, description)

    wrappedStatement.evaluate()

    verifyOrder {
      setupCmd.invoke(any())
      baseStatement.evaluate()
      teardownCmd.invoke()
    }
    confirmVerified(setupCmd, teardownCmd, baseStatement)
  }
}
