val prefix = "plugins"
listOf(
  "core",
).forEach {
  include("$prefix-$it")
  project(":$prefix-$it").projectDir = file(it)
}
