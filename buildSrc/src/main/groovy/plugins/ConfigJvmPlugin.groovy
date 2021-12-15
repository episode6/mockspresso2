package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConfigJvmPlugin implements Plugin<Project> {
  @Override
  void apply(Project target) {
    target.with {
      plugins.with {
        apply("org.jetbrains.kotlin.jvm")
      }
      compileKotlin {
        kotlinOptions {
          jvmTarget = Config.Jvm.name
          freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        }
        sourceCompatibility = Config.Jvm.name
      }
      java {
        sourceCompatibility = Config.Jvm.sourceCompat
        targetCompatibility = Config.Jvm.targetCompat
      }
      test {
        testLogging {
          events "passed", "skipped", "failed"
        }
      }

      dependencies {
        testImplementation(libs.assertk.jvm)
      }

    }
  }
}
