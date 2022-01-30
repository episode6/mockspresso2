description = "Mockspresso2 plugins for javax.inject."

plugins {
  id("config-jvm-deploy")
}

dependencies {
  api(project(":api"))

  implementation(project(":plugins-core"))
  implementation(project(":reflect"))
  implementation(libs.javax.inject.core)

  testImplementation(libs.junit5.core)
  testImplementation(libs.mockk.core)
  testImplementation(project(":core"))
}

tasks.test {
  // Enable JUnit 5
  useJUnitPlatform()
}
