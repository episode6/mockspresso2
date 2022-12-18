description = "Automatic factory support for mockspresso2 using mockito."

plugins {
  id("config-jvm-deploy")
}

dependencies {
  api(project(":api"))

  implementation(project(":reflect"))
  implementation(libs.mockito.core)
  implementation(libs.mockito.kotlin)
  implementation(libs.mockito.inline)

  testImplementation(libs.mockito.inline)
  testImplementation(libs.junit5.core)
  testImplementation(project(":core"))
}
tasks.test {
  // Enable JUnit 5
  useJUnitPlatform()
}
