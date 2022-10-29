plugins {
  kotlin("multiplatform") version (libs.versions.kotlin.core.get()) apply (false)
  id("org.jetbrains.dokka") version (libs.versions.dokka.core.get())
}

allprojects {
  group = "com.episode6.mockspresso2"
  version = "2.0.0-SNAPSHOT"
}
description =
  "A testing tool designed to reduce friction, boiler-plate and brittleness in unit tests. It's like dependency injection for your tests!"

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}

val dokkaDir = "${rootProject.buildDir}/dokka/html"
val siteDir = "${rootProject.buildDir}/site"

tasks.create<Delete>("clearDokkaDir") {
  delete(dokkaDir)
  doLast { file(dokkaDir).mkdirs() }
}

tasks.create<Delete>("clearSiteDir") {
  delete(siteDir)
  doLast { file(siteDir).mkdirs() }
}

tasks.dokkaHtmlMultiModule {
  dependsOn("clearDokkaDir")
  outputDirectory.set(file(dokkaDir))
}

tasks.create<Copy>("copyReadmes") {
  from(file("docs/"))
  exclude(".gitignore", "_site/", "_config.yml")
  into(file(siteDir))
}

tasks.create("configReadmes") {
  dependsOn("copyReadmes")
  doLast {
    file("$siteDir/_config.yml").writeText(
      """
        theme: jekyll-theme-cayman
        title: mockspresso2
        description: ${rootProject.description}
        version: $version
        docsDir: https://episode6.github.io/mockspresso2/docs/${if (Config.Maven.isReleaseBuild(project)) "v$version" else "main"}
        kotlinVersion: ${libs.versions.kotlin.core.get()}
      """.trimIndent()
    )
  }
}

tasks.create("syncDocs") {
  dependsOn("dokkaHtmlMultiModule", "configReadmes")
}
