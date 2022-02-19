## Get Up & Running
 - See the [Project Setup](PROJECT_SETUP.md) doc for current version & setup instructions.
 - See the [Getting Started](PROJECT_SETUP.md) doc to start writing tests.

## What & Why?
Mockspresso2 acts like a smart, single-use DI graph for kotlin unit and integration tests where any missing dependencies can be mocked automatically. The goal is to reduce the friction, boilerplate, brittleness and barrier-to-entry when writing unit-tests, enabling engineers to focus on what matters.

 - Avoid calling constructors (and the dreaded `lateinit var`)
 - Only mention dependencies/mocks that have some bearing on the test
 - Automatically handle common DI-related types (Provider, Lazy, Factories, etc.) 


```diff
 class CoffeeMakerHeaterTest {
+    val mxo = MockspressoBuilder().build()
 
-    val heater: Heater = mock()
+    val heater: Heater by mxo.mock()
 
-    // useless mocks that we only use to satisfy the constructor
-    val filter: Filter = mock()
-    val timer: Timer = mock()
-    val analytics: Analytics = mock()
-    val heaterProvider: Provider<Heater> = mock {
-         on { get() } doReturn heater
-    }
 
+    // let mockspresso construct the object-under-test lazily
+    val coffeeMaker: CoffeeMaker by mxo.realInstance()
-    lateinit var coffeeMaker: CoffeeMaker
 
-    @Before
-    fun setup() {
-      coffeeMaker = CoffeeMaker(heaterProvider, filter, timer, analytics)
-    }
-
     @Test fun testHeaterIsUser() {
         val coffee = coffeeMaker.brew()

         verify(heater).heat(any())
     }
 }
```

A secondary goal is to act as a vehicle to share common test code and utilities. Because Mockspresso acts as in-place of a DI-graph, Mockspresso plugins are able to register/inject test objects or mocks into that graph (which will then be injected into a real object)...

```diff
class CoffeeMakerHeaterTest {
     val mxo = MockspressoBuilder()
+        .fakeCoffeeFilter()  // named extension methods on the Builder allows for simple sharing
+        .testDispatcher()    // of test code/resources/plugins via discoverable composition
         .build()
 }
```


## Why v2
Mockspresso2 is a complete kotlin re-imagining of [Mockspresso1](https://episode6.github.io/mockspresso) (which was originally 100% Java). The key differences include...

| v1 | v2 |
| - | - |
| Java / JVM only | Kotlin Mutli-Platform* |
| Requires JUnit4 | Completely agnostic to test framework |
| Uses annotation processing on tests | Uses kotlin delegated properties |

\*We're currently only generating jvm modules but more platform support is coming soon.

See the [Translation Guide](TRANSLATION_GUIDE.md) for key syntax differences between Mockspresso 1 vs 2.


## More Resources

- [Project Setup](PROJECT_SETUP.md)
- [Getting Started](GETTING_STARTED.md)
- [Plugins](PLUGINS.md)
- [KDocs](dokka/)
- [ChangeLog](CHANGELOG.md)

## License

Mockspresso is licensed under the [MIT License](https://github.com/episode6/mockspresso2/blob/master/LICENSE)
