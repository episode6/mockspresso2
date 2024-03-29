import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom

class Config {
  class Jvm {
    static String name = "1.8"
    static JavaVersion targetCompat = JavaVersion.VERSION_1_8
    static JavaVersion sourceCompat = JavaVersion.VERSION_1_8
  }

  class Site {
    static String generateJekyllConfig(Project project) {
      return """
        theme: jekyll-theme-cayman
        title: mockspresso2
        description: ${project.rootProject.description}
        version: ${project.version}
        docsDir: https://episode6.github.io/mockspresso2/docs/${if (Maven.isReleaseBuild(project)) "v${project.version}" else "main"}
        kotlinVersion: ${project.libs.versions.kotlin.core.get()}
""".stripIndent()
    }
  }

  class Maven {
    static void applyPomConfig(Project project, MavenPom pom) {
      pom.with {
        name = project.rootProject.name + "-" + project.name
        url = "https://github.com/episode6/mockspresso2"
        licenses {
          license {
            name = "The MIT License (MIT)"
            url = "https://github.com/episode6/mockspresso2/blob/main/LICENSE"
            distribution = "repo"
          }
        }
        developers {
          developer {
            id = "episode6"
            name = "episode6, Inc."
          }
        }
        scm {
          url = "extensible"
          connection = "scm:https://github.com/episode6/mockspresso2.git"
          developerConnection = "scm:https://github.com/episode6/mockspresso2.git"
        }
      }
      project.afterEvaluate {
        pom.description = project.description ?: project.rootProject.description
      }
    }

    static boolean isReleaseBuild(Project project) {
      return project.version.contains("SNAPSHOT") == false
    }

    static String getRepoUrl(Project project) {
      if (isReleaseBuild(project)) {
        return "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
      } else {
        return "https://oss.sonatype.org/content/repositories/snapshots/"
      }
    }
  }
}
