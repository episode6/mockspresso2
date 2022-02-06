## What & Why?
Mockspresso2 acts like a single-use DI graph for kotlin unit and integration tests. Dependencies and mocks are registered in the graph during test setup and real objects are created using reflection, automatically injected with those dependencies. Any dependencies not explicitly registered can be automatically mocked. Created real objects are imported into the graph and can be combined to perform complex integration tests.

The primary goal is to reduce the friction, boilerplate, brittleness and barrier-to-entry when writing and updating unit-tests. Enabling engineers to focus on what matters...

```diff
 class CoffeeMakerHeaterTest {
+    val mxo = MockspressoBuilder().build()

+    val heater: Heater by mxo.mock()
-    val heater: Heater = mock()
-    val filter: Filter = mock()
-    val timer: Timer = mock()
-    val analytics: Analytics = mock()

+    val coffeeMaker: CoffeeMaker by mxo.realInstance()
-    lateinit var coffeeMaker: CoffeeMaker

-    @Before
-    fun setup() {
-      coffeeMaker = CoffeeMaker(heater, filter, timer, analytics)
-    }
-
     @Test fun testHeaterIsUser() {
         val coffee = coffeeMaker.brew()

         verify(heater).heat(any())
     }
 }
```

A secondary goal is to act as a vehicle to share common test code and utilities...

```diff
class CoffeeMakerHeaterTest {
     val mxo = MockspressoBuilder()
+        .fakeCoffeeFilter()  // named extension methods on the Builder allows for simple sharing
+        .testDispatcher()    // of test code/resources/plugins via discoverable composition
         .build()
 }
```


## Why v2
Mockspresso2 is a complete kotlin re-write of [Mockspresso1](https://episode6.github.io/mockspresso). The key differences include

| v1 | v2 |
| - | - |
| Java only | Kotlin Mutli-Platform (currently only generating jvm modules but more platform support to come) |
| Requires JUnit4 | Completely agnostic to test framework |
| Uses annotation processing on tests | Uses kotlin delegated properties |

See the [Translation Guide](TRANSLATION_GUIDE.md) for key syntax differences.

## Installation
```groovy
def version = '0.2.0-alpha01-SNAPSHOT'
repositories { mavenCentral() }
dependencies {

    // In a multi-module project it can be helpful to expose the api w/o exposing the entry-point in core
    api "com.episode6.mockspresso2:api:$version"

    // core module is required
    implementation "com.episode6.mockspresso2:core:$version"

    // pick a support module for your mocking framework of choice
    implementation "com.episode6.mockspresso2:plugins-mockito:$version"
    implementation "com.episode6.mockspresso2:plugins-mockk:$version"

    // optionally include plugins for popular 3rd party libs
    implementation "com.episode6.mockspresso2:plugins-dagger:$version"
    implementation "com.episode6.mockspresso2:plugins-javax-inject:$version"

    // optionally include plugins for your test framework
    implementation "com.episode6.mockspresso2:plugins-junit4:$version"
    implementation "com.episode6.mockspresso2:plugins-junit5:$version"

    // other utility plugins include
    implementation "com.episode6.mockspresso2:plugins-mockito-factories:$version"
}
```


## More Resources

- [Getting Started](GETTING_STARTED.md)
- [Plugins](PLUGINS.md)
- [KDocs](dokka/)
- [ChangeLog](CHANGELOG.md)

## License

Mockspresso is licensed under the [MIT License](https://github.com/episode6/mockspresso2/blob/master/LICENSE)
