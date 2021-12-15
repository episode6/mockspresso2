tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}

tasks.wrapper {
  gradleVersion = libs.versions.gradle.core.get()
  distributionType = Wrapper.DistributionType.ALL
}

allprojects {
  group = "com.episode6.mockspresso2"
  version = "0.0.1-SNAPSHOT"
}