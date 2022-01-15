val prefix = "plugins"
listOf(
  "core",
  "junit4"
).forEach {
  include("$prefix-$it")
  project(":$prefix-$it").projectDir = file(it)
}
