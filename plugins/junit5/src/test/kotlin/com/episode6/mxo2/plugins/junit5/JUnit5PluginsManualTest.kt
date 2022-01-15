package com.episode6.mxo2.plugins.junit5

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.MockspressoInstance
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext

class JUnit5PluginsManualTest {
  private val setupCmd: (MockspressoInstance) -> Unit = mockk(relaxed = true)
  private val teardownCmd: () -> Unit = mockk(relaxed = true)
  private val mxoExt = MockspressoBuilder()
    .onSetup(setupCmd)
    .onTeardown(teardownCmd)
    .build().junitExtension()
  private val extensionContext: ExtensionContext = mockk()

  @Test fun verifyNoOpMeansNoOp() {
    confirmVerified(setupCmd, teardownCmd)
  }

  @Test fun testBeforeEach() {
    mxoExt.beforeEach(extensionContext)

    verify {
      setupCmd.invoke(any())
    }
    confirmVerified(setupCmd, teardownCmd)
  }

  @Test fun testAfterEach() {
    mxoExt.afterEach(extensionContext)

    verifyOrder {
      setupCmd.invoke(any())
      teardownCmd.invoke()
    }
    confirmVerified(setupCmd, teardownCmd)
  }
}
