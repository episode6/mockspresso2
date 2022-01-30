val prefix = "plugins"
listOf(
  "core",
  "junit4",
  "junit5",
  "mockito",
  "mockk",
).forEach {
  include("$prefix-$it")
  project(":$prefix-$it").projectDir = file(it)
}
