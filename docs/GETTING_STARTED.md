## Installation
See the [Project Setup](PROJECT_SETUP) doc for current version & setup instructions.

### Declaring a real object

In unit tests we're usually forced to declare all our dependencies first before we can declare our unit-under-test. With mockspresso, every unit test can declare it's real object(s) up front using the [`MockspressoProperties.realInstance()`](dokka/api/com.episode6.mxo2/-mockspresso-properties/index.html#202506020%2FExtensions%2F2089714443) method.

```kotlin
class CoffeeMakerTest {
  // See the Project Setup instructions re: setting up a custom 
  // MockspressoBuilder entry-point for your project
  val mxo = MockspressoBuilder().build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()
}
```

Assuming we've set up [fallback mocking](PROJECT_SETUP#auto-mock-support), we can actually start writing tests immediately and only add dependencies as we start requiring them. The CoffeeMaker will be supplied with all mocks until we start declaring dependencies.

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

The [`plugins-mockito`](dokka/plugins-mockito/com.episode6.mxo2.plugins.mockito/index.html) and [`plugins-mockk`](dokka/plugins-mockk/com.episode6.mxo2.plugins.mockk/index.html) modules include a few plugins to assist with mocking dependencies. 

 - [`MockspressoBuilder.defaultMock`](dokka/plugins-mockito/com.episode6.mxo2.plugins.mockito/index.html#-1930091915%2FFunctions%2F37435277) / [`defaultMockk`](dokka/plugins-mockk/com.episode6.mxo2.plugins.mockk/index.html#210609015%2FFunctions%2F147516529)
 - [`MockspressoProperties.mock`](dokka/plugins-mockito/com.episode6.mxo2.plugins.mockito/index.html#1781692779%2FFunctions%2F37435277) / [`mockk`](dokka/plugins-mockk/com.episode6.mxo2.plugins.mockk/index.html#2054217256%2FFunctions%2F147516529)
 - [`MockspressoProperties.spy`](dokka/plugins-mockito/com.episode6.mxo2.plugins.mockito/index.html#-1963645221%2FFunctions%2F37435277) / [`spyk`](dokka/plugins-mockk/com.episode6.mxo2.plugins.mockk/index.html#-1266070436%2FFunctions%2F147516529)

 In each case the plugin signature mirrors what is supplied by the mocking framework. Example (using mockito):

 ```kotlin
class CoffeeMakerTest {
  val mxo = MockspressoBuilder()
     // mock an executor that we don't need a reference to but needs some setup
    .defaultMock<Executor> {
      on { execute(any()) } doAnswer { it.getArgument(0).run() }
    }.build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()

  // let mockspresso create a real Filter, then wrap it with Mockito.spy
  val filter: Filter by mxo.spy()

  // mock a heater and include it as a dependency
  val heater: Heater by mxo.mock()

  @Test fun testHeater() {
    coffeeMaker.brew()

    verify(filter).clean()
    verify(heater).heat(any())
  }
}
```

### Integration tests

When we create or declare any real object in mockspresso, that object becomes part of the underlying dependency graph. This means we can declare multiple real objects and run complex integration tests w/o ever worrying about constructor or dependency order. It also means our integrations tests won't break by default just because dependencies have changed.

```kotlin
class CoffeeMakerTest {
  val mxo = MockspressoBuilder()
    .realInstanceOf<Filter>() // a real Filter will be constructed
    .realImplementationOf<Heater, HeaterImpl>() // a real HeaterImpl will be constructed
    .build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()

  // a real FastGrinder will be constructed
  val grinder: FastGrinder by mxo.realImplOf<Grinder, FastGrinder>()
}
```

### Qualifier Annotations
All of the methods shown above actually include an optional qualifier `Annotation?` as their first parameter. That is because every DI binding in mockspresso (aka [`DependencyKey`](dokka/api/com.episode6.mxo2.reflect/index.html#-1902283991%2FClasslikes%2F2089714443)) is made up of both a [`TypeToken`](dokka/api/com.episode6.mxo2.reflect/index.html#-873316418%2FClasslikes%2F2089714443) and an optional qualifier `Annotation?`. In the JVM, this means the annotation must, itself, be annotated with `javax.inject.Qualifier`.

Example:
```kotlin
// real class
class MyRealClass @Inject constructor(
  // Named is stock qualifier annotation included in jsr-330
  @Named("IO") ioDispatcher: CoroutineContext
)

// test class
class MyRealClassTest {
  val mxo = MockspressoBuilder()
    .dependencyOf<CoroutineContext>(createAnnotation<Named>("IO")) { EmptyCoroutineContext }
    .build()

  // will have EmptyCoroutineContext injected as dependency
  val myRealClass: MyRealClass by mxo.realInstance()
}
```
**Note:** The `createAnnotation()` method is part of `kotlin-reflect`

### Developing Plugins

Mockspresso plugins are just kotlin extension functions targeting one of Mockspresso's primary interfaces.

More Common
 - [`MockspressoBuilder`](dokka/api/com.episode6.mxo2/index.html#-1308321104%2FClasslikes%2F2089714443) for plugins that add to the dependency graph but don't need to return anything (builder plugins must always return the builder).
 - [`MockspressoProperties`](dokka/api/com.episode6.mxo2/index.html#1185097316%2FClasslikes%2F2089714443) for plugins that need to add the dependency graph but also return a (lazy) reference.

 Less Common
 - [`MockspressoInstance`](dokka/api/com.episode6.mxo2/index.html#-1651402046%2FClasslikes%2F2089714443) for plugins that only need to pull from the dependency graph but do not need to add to it.
 - [`Mockspresso`](dokka/api/com.episode6.mxo2/index.html#616616919%2FClasslikes%2F2089714443) for test-framework support plugins that control the test lifecycle

 Some plugin examples...
 ```kotlin
// MockspressoBuilder plugin to inject an EmptyCoroutineContext that is bound 
// in DI as CoroutineContext
fun MockspressoBuilder.emptyCoroutineContext(qualifier: Annotation? = null): MockspressoBuilder = 
  dependencyOf<CoroutineContext>(qualifier) { EmptyCoroutineContext }

// Usage: 
val mxo = MockspressoBuilder()
  .emptyCoroutineContext(createAnnotation<Named>("IO"))
  .build()
```


```kotlin
// MockspressoProperties plugin to return a lazy of a TestCoroutineContext that is bound 
// in DI as CoroutineContext
fun MockspressoProperties.testCoroutineContext(qualifier: Annotation? = null): Lazy<TestCoroutineContext> = 
  fakeOf<CoroutineContext, TestCoroutineContext>(qualifier) { TestCoroutineContext() }

// Usage: (in the real tests we could drop the type)
val context: TestCoroutineContext by mxo.testCoroutineContext(createAnnotation<Named>("IO"))
```
 
There are also 3 types of plugins that can only be applied to [`MockspressoBuilder`](dokka/api/com.episode6.mxo2/index.html#-1308321104%2FClasslikes%2F2089714443)
 - [`RealObjectMaker`](dokka/api/com.episode6.mxo2/-mockspresso-builder/index.html#-473334580%2FFunctions%2F2089714443) creates real objects using some pre-defined ruleset
 - [`FallbackObjectMaker`](dokka/api/com.episode6.mxo2/-mockspresso-builder/index.html#653129548%2FFunctions%2F2089714443) creates objects (usually mocks) that aren't explicitly registered in the graph
 - [`DynamicObjectMaker`](dokka/api/com.episode6.mxo2/-mockspresso-builder/index.html#-281366160%2FFunctions%2F2089714443) gets a chance to create any objects that aren't explicitly registered in the graph

 The [`DynamicObjectMaker`](dokka/api/com.episode6.mxo2.api/-dynamic-object-maker/index.html) is worth calling out as it's one of mockspresso's more powerful concepts and powers the support of [`javax.inject.Provider`](dokka/plugins-javax-inject/com.episode6.mxo2.plugins.javax.inject/index.html#-870338652%2FFunctions%2F-1937516557), [`dagger.Lazy`](dokka/plugins-dagger2/com.episode6.mxo2.plugins.dagger2/index.html#364970602%2FFunctions%2F341024319) and [`@dagger.AssistedFactory`](dokka/plugins-mockito-factories/com.episode6.mxo2.plugins.mockito.factories/index.html#-1170205750%2FFunctions%2F1534461010).
