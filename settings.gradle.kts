enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
  versionCatalogs {
    create("libs") { from(files("libs.versions.toml")) }
  }
}

rootProject.name = "mockspresso2"

include(
  ":api",
  ":core",
  ":reflect"
)
apply {
  from("plugins/settings.gradle.kts")
}
