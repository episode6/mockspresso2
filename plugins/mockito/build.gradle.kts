description = "Mockspresso2 plugins for junit5."

plugins {
  id("config-jvm-deploy")
}

dependencies {
  api(project(":api"))

  implementation(libs.mockito.core)
  implementation(libs.mockito.kotlin)

  testImplementation(libs.mockito.inline)
  testImplementation(libs.junit5.core)
  testImplementation(project(":core"))
}
tasks.test {
  // Enable JUnit 5
  useJUnitPlatform()
}
