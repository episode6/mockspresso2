plugins {
  kotlin("multiplatform") version (libs.versions.kotlin.core.get()) apply (false)
  id("org.jetbrains.dokka") version (libs.versions.dokka.core.get())
  id("config-site")
}

allprojects {
  group = "com.episode6.mockspresso2"
  version = "2.0.0-SNAPSHOT"
}
description =
  "A testing tool designed to reduce friction, boiler-plate and brittleness in unit tests. It's like dependency injection for your tests!"

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}

val chopChangelog = tasks.create("chopChangelog") {
  val allContent = file("$rootDir/docs/CHANGELOG.md").readLines()
  val firstHeaderIndex = allContent.indexOfFirst { it.startsWith("###") }
  val topContent = allContent.drop(firstHeaderIndex+1)
  val nextHeaderIndex = topContent.indexOfFirst { it.startsWith("###") }
  val versionContent = listOf("### Changelog") + topContent.subList(0, nextHeaderIndex)
  file("$rootDir/VERSION_CHANGELOG.md").writeText(versionContent.joinToString(separator = "\n"))
}

tasks.getByName("syncDocs") {
  dependsOn(chopChangelog)
}
