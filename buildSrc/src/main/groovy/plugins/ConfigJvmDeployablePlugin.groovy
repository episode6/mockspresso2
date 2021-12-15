package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar

class ConfigJvmDeployablePlugin implements Plugin<Project> {
  @Override
  void apply(Project target) {
    target.with {
      plugins.with {
        apply(ConfigJvmPlugin)
        apply(CommonDeployablePlugin)
      }

      task("sourcesJar", type: Jar) {
        from sourceSets.main.allSource
        archiveClassifier.set('sources')
      }

      publishing {
        publications {
          mavenJava(MavenPublication) {
            from project.components.java
            Config.Maven.applyPomConfig(target, pom)
            artifact sourcesJar
            artifact javadocJar
          }
        }
      }
    }
  }
}
