package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConfigMultiPlugin implements Plugin<Project> {
  @Override
  void apply(Project target) {
    target.with {
      plugins.with {
        apply("org.jetbrains.kotlin.multiplatform")
      }
      kotlin {
        jvm  {
          compilations.all {
            kotlinOptions {
              jvmTarget = Config.Jvm.name
            }
          }
          java {
            sourceCompatibility = Config.Jvm.sourceCompat
            targetCompatibility = Config.Jvm.targetCompat
          }
          jvmTest {
            useJUnitPlatform()
            testLogging {
              events "passed", "skipped", "failed"
            }
          }
        }

        sourceSets {
          commonMain {}
          commonTest {
            dependencies {
              implementation(kotlin("test"))
              implementation(libs.assertk.core)
            }
          }
          jvmMain {}
          jvmTest {
            dependencies {
              implementation(libs.junit5.core)
              implementation(libs.assertk.jvm)
            }
          }
        }

      }
    }
  }
}
