description = "Public api of Mockspresso2, a testing tool designed to reduce friction, boiler-plate and brittleness in unit tests."

plugins {
  id("config-multi-deploy")
}

kotlin {
  sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
  }
}
