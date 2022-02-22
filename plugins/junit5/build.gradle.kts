description = "Mockspresso2 plugins for junit5."

plugins {
  id("config-jvm-deploy")
}

dependencies {
  api(project(":api"))

  implementation(libs.junit5.core)

  testImplementation(libs.junit5.core)
  testImplementation(libs.mockk.core)
  testImplementation(project(":core"))
}
tasks.test {
  // Enable JUnit 5
  useJUnitPlatform()
}
