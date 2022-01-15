package com.episode6.mxo2.plugins.junit5

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.MockspressoInstance
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

// WARNING, this class may only contain a single Test function
class JUnit5ExtensionTest {

  companion object {
    private val setupCmd: (MockspressoInstance) -> Unit = mockk(relaxed = true)
    private val teardownCmd: () -> Unit = mockk(relaxed = true)

    @BeforeAll @JvmStatic fun beforeAll() {
      confirmVerified(setupCmd, teardownCmd)
    }

    @AfterAll @JvmStatic fun afterAll() {
      verify { teardownCmd.invoke() }
      confirmVerified(setupCmd, teardownCmd)
    }
  }
  @RegisterExtension val mxoExt = MockspressoBuilder()
    .onSetup(setupCmd)
    .onTeardown(teardownCmd)
    .build().junitExtension()

  @Test fun testDuringTest() {
    verify { setupCmd.invoke(any()) }
    confirmVerified(setupCmd, teardownCmd)
  }
}
