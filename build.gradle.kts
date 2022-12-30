plugins {
  kotlin("multiplatform") version (libs.versions.kotlin.core.get()) apply (false)
  id("org.jetbrains.dokka") version (libs.versions.dokka.core.get())
  id("config-site")
}

allprojects {
  group = "com.episode6.mockspresso2"
  version = "2.0.1-SNAPSHOT"
}
description =
  "A testing tool designed to reduce friction, boiler-plate and brittleness in unit tests. It's like dependency injection for your tests!"

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}
