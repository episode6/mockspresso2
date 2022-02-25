plugins {
  id("org.jetbrains.dokka")
}

allprojects {
  group = "com.episode6.mockspresso2"
  version = "2.0.0-alpha02"
}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}

val dokkaDir = "docs/dokka"

tasks.create<Delete>("clearDocsDir") {
  delete(dokkaDir)
  doLast { file("$rootDir/$dokkaDir").mkdirs() }
}

tasks.dokkaHtmlMultiModule {
  dependsOn("clearDocsDir")
  outputDirectory.set(file("$rootDir/$dokkaDir"))
}

tasks.create("syncDocs") {
  dependsOn("dokkaHtmlMultiModule")
}
