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

      kotlin {
        def jvmTargetClass = it.class.classLoader.loadClass("org.jetbrains.kotlin.gradle.dsl.JvmTarget")
        compilerOptions {
          jvmTarget.set(jvmTargetClass.fromTarget(Config.Jvm.name))
          freeCompilerArgs.add(Config.Kotlin.compilerArgs)
        }
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
        testRuntimeOnly(libs.junit5.launcher)
      }

    }
  }
}
