plugins {
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.dokka)
  id("config-site")
}

allprojects {
  group = "com.episode6.mockspresso2"
  version = "2.1.0-SNAPSHOT"
}
description =
  "A testing tool designed to reduce friction, boiler-plate and brittleness in unit tests. It's like dependency injection for your tests!"

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}
