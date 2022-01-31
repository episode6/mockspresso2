description = "Mockspresso2 plugins for javax.inject."

plugins {
  id("config-jvm-deploy")
}

dependencies {
  api(project(":api"))

  implementation(project(":plugins-javax-inject"))
  implementation(project(":reflect"))
  implementation(libs.dagger2.core)

  testImplementation(libs.junit5.core)
  testImplementation(libs.mockk.core)
  testImplementation(project(":core"))
}

tasks.test {
  // Enable JUnit 5
  useJUnitPlatform()
}
