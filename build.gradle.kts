plugins {
  id("org.jetbrains.dokka")
}

allprojects {
  group = "com.episode6.mockspresso2"
  version = "2.0.0-alpha07-SNAPSHOT"
}
description = "A testing tool designed to reduce friction, boiler-plate and brittleness in unit tests. It's like dependency injection for your tests!"

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}

val dokkaDir = "build/dokka/html"

tasks.create<Delete>("clearDocsDir") {
  delete(dokkaDir)
  doLast { file("$rootDir/$dokkaDir").mkdirs() }
}

tasks.dokkaHtmlMultiModule {
  dependsOn("clearDocsDir")
  outputDirectory.set(file("$rootDir/$dokkaDir"))
}

tasks.create("configDocs") {
  doLast {
    file("$rootDir/docs/_config.yml").writeText(
      """
        theme: jekyll-theme-cayman
        title: mockspresso2
        description: $description
        version: $version
        docsDir: https://episode6.github.io/mockspresso2/docs/${ if (Config.Maven.isReleaseBuild(project)) "v$version" else "main" }
        kotlinVersion: ${libs.versions.kotlin.core.get()}
      """.trimIndent()
    )
  }
}

tasks.create("syncDocs") {
  dependsOn("dokkaHtmlMultiModule", "configDocs")
}
