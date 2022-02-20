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
 - [`MockspressoBuilder.dependencyOf`](dokka/api/com.episode6.mxo2/-mockspresso-builder/index.html#1507930812%2FExtensions%2F2089714443): if the reference **is not** needed for the test 
 - [`MockspressoProperties.depOf`](dokka/api/com.episode6.mxo2/-mockspresso-properties/index.html#27288324%2FExtensions%2F2089714443): if the reference **is** needed for the test
 - [`MockspressoProperties.fakeOf<BIND, IMPL>`](dokka/api/com.episode6.mxo2/-mockspresso-properties/index.html#-1175481446%2FExtensions%2F2089714443): if the reference needed by the test must be of a different type than the type of dependency bound in the real object

```kotlin
class CoffeeMakerTest {
  val mxo = MockspressoBuilder()
    .dependencyOf<Timer> { FakeTimer() }
    .build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()

  // TestFilter doesn't have any special methods we need
  val filter: Filter by mxo.depOf<Filter> { TestFilter() }

  // We need access to TestHeater.finishHeating()
  val heater: TestHeater by mxo.fakeOf<Heater, TestHeater> { TestHeater() }
}
```
**Note:** The `filter` and `heater` examples above should be rare in actual tests and would usually be substituted by plugins.

### Declaring mock dependencies

The [`plugins-mockito`](dokka/plugins-mockito/com.episode6.mxo2.plugins.mockito/index.html) and [`plugins-mockk`](dokka/plugins-mockk/com.episode6.mxo2.plugins.mockk/index.html) modules (aside from providing auto-mock support) include a few plugins to assist with mocking dependencies.

 - `MockspressoBuilder.defaultMock` / `defaultMockk`
 - `MockspressoProperties.mock` / `mockk`
 - `MockspressoProperties.spy` / `spyk`