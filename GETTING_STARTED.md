## Installation
See the [Project Setup](PROJECT_SETUP.md) doc for current version & setup instructions.

### Understanding the basics

Mockspresso creates real objects by scanning the constructor parameters and translating them into [`DependencyKey`]({{ site.docsDir }}/api/com.episode6.mockspresso2.reflect/-dependency-key/index.html)s (which includes their type information and an optional [Qualifier annotation](#qualifier-annotations)). Tests "register" the dependencies they care about in a [`MockspressoBuilder`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-1956183425%2FClasslikes%2F2089714443), and any dependencies that are missing from the registry are supplied by the [`FallbackObjectMaker`]({{ site.docsDir }}/api/com.episode6.mockspresso2/-mockspresso-builder/index.html#-740171218%2FFunctions%2F2089714443) (usually with a mock).

When we interact with Mockspresso in a test it's in one of 3 forms...

 - [`MockspressoInstance`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-260297517%2FClasslikes%2F2089714443) is like an immutable `HashMap` where the key is a [`DependencyKey`]({{ site.docsDir }}/api/com.episode6.mockspresso2.reflect/-dependency-key/index.html) and the value is the dependency itself. (note: we rarely interact with `MockspressoInstance` in unit tests except for special edge-cases)
 - [`MockspressoBuilder`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-1956183425%2FClasslikes%2F2089714443) is what we use to put dependencies into a [`MockspressoInstance`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-260297517%2FClasslikes%2F2089714443). We can only put into a Builder, we cannot read from it until it's been built.
 - [`MockspressoProperties`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-1993246667%2FClasslikes%2F2089714443) represents a middle-state between Builder and Instance. The MockspressoInstance is ensured lazily under the hood, and the MockspressoBuilder remains mutable until that happens. When adding dependencies / declaring real objects on [`MockspressoProperties`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-1993246667%2FClasslikes%2F2089714443), a `kotlin.Lazy` is returned, granting a reference to the new object (note: referencing this `Lazy.value` will force the `MockspressoInstance` to be ensured if it hasn't already). 


### Declaring a real object

In unit tests we're usually forced to declare all our dependencies first before we can declare our unit-under-test. With mockspresso, every unit test can declare it's real object(s) up front using the [`MockspressoProperties.realInstance()`]({{ site.docsDir }}/api/com.episode6.mockspresso2/-mockspresso-properties/index.html#-42928657%2FExtensions%2F2089714443) method.

```kotlin
class CoffeeMakerTest {
  // See the Project Setup instructions re: setting up a custom 
  // MockspressoBuilder entry-point for your project
  val mxo = MockspressoBuilder().build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()
}
```

Assuming we've set up [fallback mocking](PROJECT_SETUP.md#auto-mock-support), we can actually start writing tests immediately and only add dependencies as we start requiring them. The CoffeeMaker will be supplied with all mocks until we start declaring dependencies.

### Declaring dependencies

Dependencies for the real object can be declared using either
 - [`MockspressoBuilder.dependency`]({{ site.docsDir }}/api/com.episode6.mockspresso2/-mockspresso-builder/index.html#-2124238825%2FExtensions%2F2089714443): if the reference **is not** needed for the test 
 - [`MockspressoProperties.dependency`]({{ site.docsDir }}/api/com.episode6.mockspresso2/-mockspresso-properties/index.html#-1138087455%2FExtensions%2F2089714443): if the reference **is** needed for the test
 - [`MockspressoProperties.fake<BIND, IMPL>`]({{ site.docsDir }}/api/com.episode6.mockspresso2/-mockspresso-properties/index.html#-1069132881%2FExtensions%2F2089714443): if the reference needed by the test must be of a different type than the type of dependency bound in the real object

```kotlin
class CoffeeMakerTest {
  val mxo = MockspressoBuilder()
    .dependency<Timer> { FakeTimer() }
    .build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()

  // TestFilter doesn't have any special methods we need
  val filter: Filter by mxo.dependency<Filter> { TestFilter() }

  // We need access to TestHeater.finishHeating()
  val heater: TestHeater by mxo.fake<Heater, TestHeater> { TestHeater() }
}
```
**Note:** The `filter` and `heater` examples above should be rare in actual tests and would usually be substituted by plugins.

### Declaring mock dependencies

The [`plugins-mockito`]({{ site.docsDir }}/plugins-mockito/com.episode6.mockspresso2.plugins.mockito/index.html) and [`plugins-mockk`]({{ site.docsDir }}/plugins-mockk/com.episode6.mockspresso2.plugins.mockk/index.html) modules include a few plugins to assist with mocking dependencies. 

 - [`MockspressoBuilder.mock`]({{ site.docsDir }}/plugins-mockito/com.episode6.mockspresso2.plugins.mockito/index.html#-232878768%2FFunctions%2F37435277) / [`mockk`]({{ site.docsDir }}/plugins-mockk/com.episode6.mockspresso2.plugins.mockk/index.html#1418851012%2FFunctions%2F147516529) if the reference **is not** needed for the test
 - [`MockspressoProperties.mock`]({{ site.docsDir }}/plugins-mockito/com.episode6.mockspresso2.plugins.mockito/index.html#-232878768%2FFunctions%2F37435277) / [`mockk`]({{ site.docsDir }}/plugins-mockk/com.episode6.mockspresso2.plugins.mockk/index.html#1418851012%2FFunctions%2F147516529) if the reference **is** needed for the test
 - [`MockspressoProperties.spy`]({{ site.docsDir }}/plugins-mockito/com.episode6.mockspresso2.plugins.mockito/index.html#-877399269%2FFunctions%2F37435277) / [`spyk`]({{ site.docsDir }}/plugins-mockk/com.episode6.mockspresso2.plugins.mockk/index.html#-642814402%2FFunctions%2F147516529) wrap a real object with a spy

 In each case the plugin signature mirrors what is supplied by the mocking framework. Example (using mockito):

 ```kotlin
class CoffeeMakerTest {
  val mxo = MockspressoBuilder()
     // mock an executor that we don't need a reference to but needs some setup
    .mockock<Executor> {
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
    .realInstance<Filter>() // a real Filter will be constructed
    .realImplementation<Heater, HeaterImpl>() // a real HeaterImpl will be constructed
    .build()

  val coffeeMaker: CoffeeMaker by mxo.realInstance()

  // a real FastGrinder will be constructed
  val grinder: FastGrinder by mxo.realImplementation<Grinder, FastGrinder>()
}
```

### Qualifier Annotations
All of the methods shown above actually include an optional qualifier `Annotation?` as their first parameter. That is because every DI binding in mockspresso (aka [`DependencyKey`]({{ site.docsDir }}/api/com.episode6.mockspresso2.reflect/index.html#-739330760%2FFunctions%2F2089714443)) is made up of both a [`TypeToken`]({{ site.docsDir }}/api/com.episode6.mockspresso2.reflect/index.html#2094150672%2FFunctions%2F2089714443) and an optional qualifier `Annotation?`. In the JVM, this means the annotation must, itself, be annotated with `javax.inject.Qualifier`.

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
    .dependency<CoroutineContext>(createAnnotation<Named>("IO")) { EmptyCoroutineContext }
    .build()

  // will have EmptyCoroutineContext injected as dependency
  val myRealClass: MyRealClass by mxo.realInstance()
}
```
**Note:** The `createAnnotation()` method is part of `kotlin-reflect`

### Developing Plugins

Mockspresso plugins are just kotlin extension functions targeting one of Mockspresso's primary interfaces.

More Common
 - [`MockspressoBuilder`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-1956183425%2FClasslikes%2F2089714443) for plugins that add to the dependency graph but don't need to return anything (builder plugins must always return the builder).
 - [`MockspressoProperties`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-1993246667%2FClasslikes%2F2089714443) for plugins that need to add the dependency graph but also return a (lazy) reference.

 Less Common
 - [`MockspressoInstance`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-260297517%2FClasslikes%2F2089714443) for plugins that only need to pull from the dependency graph but do not need to add to it.
 - [`Mockspresso`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#1594033896%2FClasslikes%2F2089714443) for test-framework support plugins that control the test lifecycle

 Some plugin examples...
 ```kotlin
// MockspressoBuilder plugin to inject an EmptyCoroutineContext that is bound 
// in DI as CoroutineContext
fun MockspressoBuilder.emptyCoroutineContext(qualifier: Annotation? = null): MockspressoBuilder = 
  dependency<CoroutineContext>(qualifier) { EmptyCoroutineContext }

// Usage: 
val mxo = MockspressoBuilder()
  .emptyCoroutineContext(createAnnotation<Named>("IO"))
  .build()
```


```kotlin
// MockspressoProperties plugin to return a lazy of a TestCoroutineContext that is bound 
// in DI as CoroutineContext
fun MockspressoProperties.testCoroutineContext(qualifier: Annotation? = null): Lazy<TestCoroutineContext> = 
  fake<CoroutineContext, TestCoroutineContext>(qualifier) { TestCoroutineContext() }

// Usage: (in the real tests we could drop the type)
val context: TestCoroutineContext by mxo.testCoroutineContext(createAnnotation<Named>("IO"))
```
 
There are also 3 types of plugins that can only be applied to [`MockspressoBuilder`]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html#-1956183425%2FClasslikes%2F2089714443)
 - [`RealObjectMaker`]({{ site.docsDir }}/api/com.episode6.mockspresso2/-mockspresso-builder/index.html#-1857907410%2FFunctions%2F2089714443) creates real objects using some pre-defined ruleset
 - [`FallbackObjectMaker`]({{ site.docsDir }}/api/com.episode6.mockspresso2/-mockspresso-builder/index.html#-740171218%2FFunctions%2F2089714443) creates objects (usually mocks) that aren't explicitly registered in the graph
 - [`DynamicObjectMaker`]({{ site.docsDir }}/api/com.episode6.mockspresso2/-mockspresso-builder/index.html#1165825806%2FFunctions%2F2089714443) gets a chance to create any objects that aren't explicitly registered in the graph

 The [`DynamicObjectMaker`]({{ site.docsDir }}/api/com.episode6.mockspresso2.api/-dynamic-object-maker/index.html) is worth calling out as it's one of mockspresso's more powerful concepts and powers the support of [`javax.inject.Provider`]({{ site.docsDir }}/plugins-javax-inject/com.episode6.mockspresso2.plugins.javax.inject/index.html#313322564%2FFunctions%2F-1937516557), [`dagger.Lazy`]({{ site.docsDir }}/plugins-dagger2/com.episode6.mockspresso2.plugins.dagger2/index.html#150692684%2FFunctions%2F341024319) and [`@dagger.AssistedFactory`]({{ site.docsDir }}/plugins-mockito-factories/com.episode6.mockspresso2.plugins.mockito.factories/index.html#1463350186%2FFunctions%2F1534461010).


### Included Plugins
See the [Plugin Modules](PLUGINS.md) doc for a full list of the plugin modules mockspresso2 ships with 
