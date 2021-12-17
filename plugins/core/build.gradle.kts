description = "Core plugins included with Mockspresso2"

plugins {
  id("config-multi-deploy")
}

kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":api"))
        implementation(project(":reflect"))
      }
    }
  }
}
