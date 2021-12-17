description = "Reflection platform for Mockspresso2. Includes needed expect/actual declarations to drive our core plugins and real object makers."

plugins {
  id("config-multi-deploy")
}

kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":api"))
        api(kotlin("reflect"))
      }
    }
    val jvmMain by getting {
      dependencies {
        implementation(libs.javax.inject.core)
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
