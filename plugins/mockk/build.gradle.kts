description = "Mockk support for Mockspresso2."

plugins {
  id("config-multi-deploy")
}

kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":api"))
        implementation(project(":reflect"))
        implementation(libs.mockk.core)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(project(":core"))
      }
    }
  }
}
