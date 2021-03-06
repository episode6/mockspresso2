plugins {
  `java-gradle-plugin`
}

dependencies {
  runtimeOnly(libs.bundles.gradle.plugins)
}

gradlePlugin {
  plugins {
    create("ConfigureJvmPlugin") {
      id = "config-jvm"
      implementationClass = "plugins.ConfigJvmPlugin"
    }
    create("ConfigureMultiPlugin") {
      id = "config-multi"
      implementationClass = "plugins.ConfigMultiPlugin"
    }
    create("ConfigureJvmDeployable") {
      id = "config-jvm-deploy"
      implementationClass = "plugins.ConfigJvmDeployablePlugin"
    }
    create("ConfigureMultiDeployable") {
      id = "config-multi-deploy"
      implementationClass = "plugins.ConfigMultiDeployablePlugin"
    }
  }
}
