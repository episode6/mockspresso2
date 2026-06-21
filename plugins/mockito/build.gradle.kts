description = "Mockspresso2 plugins for junit5."

plugins {
  id("config-jvm-deploy")
}

dependencies {
  api(project(":api"))

  implementation(project(":reflect"))
  implementation(libs.mockito.core)
  implementation(libs.mockito.kotlin)

  testImplementation(libs.mockito.inline)
  testImplementation(libs.junit5.core)
  testImplementation(project(":core"))
}
tasks.test {
  // Enable JUnit 5
  useJUnitPlatform()
  // mockito-inline (4.x) byte-buddy only supports up to Java 20; experimental flag unlocks newer JVMs
  jvmArgs("-XX:+EnableDynamicAgentLoading", "-Dnet.bytebuddy.experimental=true")
}
