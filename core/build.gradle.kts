description = "Core implementation of Mockspresso2, a testing tool designed to reduce friction, boiler-plate and brittleness in unit tests."

plugins {
  id("config-multi-deploy")
}

kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":api"))
        api(project(":plugins-core"))
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(project(":reflect"))
        implementation(libs.mockk.core)
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(libs.javax.inject.core)
        implementation(libs.kotlin.reflect)
      }
    }
  }
}
