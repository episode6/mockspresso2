package com.episode6.mockspresso2.reflect

import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test
import javax.inject.Named
import javax.inject.Qualifier

class JvmQualifierTest {

  @Test fun testNoQualifiers() {
    val qualifier = NoQualifiers::class.annotations.findQualifier { "test failure context" }

    assertThat(qualifier).isNull()
  }

  @Test fun testOneQualifier() {
    val qualifier = OneQualifier::class.annotations.findQualifier { "test failure context" }

    assertThat(qualifier).isNotNull().isInstanceOf(TestQualifierAnnotation::class)
  }

  @Test fun testOneQualifierWithName() {
    val qualifier = OneQualifierWithName::class.annotations.findQualifier { "test failure context" }

    assertThat(qualifier)
      .isNotNull()
      .isInstanceOf(Named::class)
      .transform { it.value }
      .isEqualTo("something")
  }

  @Test fun testTwoQualifiers() {
    assertThat { TwoQualifiers::class.annotations.findQualifier { "test failure context" } }
      .isFailure()
      .given {
        assertThat(it).isInstanceOf(MultipleQualifierError::class)
        assertThat(it.message).isEqualTo("Multiple qualifier annotations found: test failure context")
      }
  }

  @Test fun testTwoQualifiersWithName() {
    assertThat { TwoQualifiersWithName::class.annotations.findQualifier { "test failure context" } }
      .isFailure()
      .isInstanceOf(MultipleQualifierError::class)
      .hasMessage("Multiple qualifier annotations found: test failure context")
  }

  @Qualifier private annotation class TestQualifierAnnotation
  private annotation class TestRegAnnotation1
  private annotation class TestRegAnnotation2

  @TestRegAnnotation1 @TestRegAnnotation2 private class NoQualifiers
  @TestQualifierAnnotation @TestRegAnnotation1 @TestRegAnnotation2 private class OneQualifier
  @TestQualifierAnnotation @TestRegAnnotation1 @TestRegAnnotation2 @Named private class TwoQualifiers
  @TestQualifierAnnotation @TestRegAnnotation1 @TestRegAnnotation2 @Named("name") private class TwoQualifiersWithName
  @TestRegAnnotation1 @TestRegAnnotation2 @Named("something") private class OneQualifierWithName
}
