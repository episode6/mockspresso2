val prefix = "plugins"
listOf(
  "core",
  "dagger2",
  "javax-inject",
  "junit4",
  "junit5",
  "mockito",
  "mockito-factories",
  "mockk",
  "mockk-factories",
).forEach {
  include("$prefix-$it")
  project(":$prefix-$it").projectDir = file(it)
}
