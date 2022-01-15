description = "Mockspresso2 plugins for junit4."

plugins {
  id("config-jvm-deploy")
}

dependencies {
  api(project(":api"))

  implementation(libs.junit4.core)

  testImplementation(libs.junit4.core)
  testImplementation(libs.mockk.core)
  testImplementation(project(":core"))
}
