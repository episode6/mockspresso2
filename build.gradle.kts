plugins {
  kotlin("multiplatform") version (libs.versions.kotlin.core.get()) apply (false)
  id("org.jetbrains.dokka") version (libs.versions.dokka.core.get())
  id("config-site")
}

allprojects {
  group = "com.episode6.mockspresso2"
  version = "2.0.0-rc2"
}
description =
  "A testing tool designed to reduce friction, boiler-plate and brittleness in unit tests. It's like dependency injection for your tests!"

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}

val chopChangelog = tasks.create("chopChangelog") {
  val versionContent = file("$rootDir/docs/CHANGELOG.md").readLines()
    .let { list -> list.drop(list.indexOfFirst { it.startsWith("###") }+1) }
    .let { list -> list.subList(0, list.indexOfFirst { it.startsWith("###") }) }
    .let { listOf("### Changelog") + it }
    .joinToString(separator = "\n")
  file("$rootDir/VERSION_CHANGELOG.md").writeText(versionContent)
}

tasks.getByName("syncDocs") {
  dependsOn(chopChangelog)
}
