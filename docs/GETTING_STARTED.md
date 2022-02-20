## Installation
See the [Project Setup](PROJECT_SETUP) doc for current version & setup instructions.

## Declaring a real object

In unit tests we're usually forced to declare all our dependencies first before we can declare our unit-under-test. With mockspresso, every unit test can declare it's real object(s) up front using the [`realInstance()`](dokka/api/com.episode6.mxo2/real-instance.html) method.

```kotlin
class CoffeeMakerTest {
  // See the Project Setup instructions re: setting up a custom 
  // MockspressoBuilder entry-point for your project
  val mxo = MockspressoBuilder().build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()
}
```

Assuming we've set up [fallback mocking](PROJECT_SETUP#auto-mock-support), we can actually start writing tests immediately and only add dependencies as we start requiring them. The CoffeeMaker will be supplied with all mocks until we star declaring dependencies.

### Declaring dependencies

Dependencies for the real object can be declared using either
 - [`MockspressoBuilder.dependencyOf`](dokka/api/com.episode6.mxo2/-mockspresso-builder/index.html#1507930812%2FExtensions%2F2089714443) (if their references are not needed for the test) 
 - [`MockspressoProperties.depOf`](dokka/api/com.episode6.mxo2/-mockspresso-properties/index.html#27288324%2FExtensions%2F2089714443) (which is implemented by `mxo` and allows us to leverage kotlin's delegated properties).

```kotlin
class CoffeeMakerTest {
  val mxo = MockspressoBuilder()
    .dependencyOf<Timer> { FakeTimer() }
    .build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()

  val filter by mxo.depOf { Filter() }
}
```