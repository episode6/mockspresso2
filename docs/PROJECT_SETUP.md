## Project Setup
Ideally, mockspresso should only be exposed to tests via a dedicated gradle module. This lets us define MockspressoBuilder with defaults appropriate to our project and prevents accidental usage of the default [`MockspressoBuilder()`](dokka/core/com.episode6.mockspresso2/-mockspresso-builder.html) entry-point.

### Gradle setup
```groovy
def mxoVersion = '2.0.0-alpha06-SNAPSHOT'

repositories { maven { url "https://oss.sonatype.org/content/repositories/snapshots" } }
dependencies {

    // expose the api to other modules
    api "com.episode6.mockspresso2:api:$mxoVersion"

    // hide the core module from other modules
    implementation "com.episode6.mockspresso2:core:$mxoVersion"
}
```

### Entry-point in dedicated gradle module
```kotlin
package com.sample.myproject.testsupport

// define a simple custom entry-point for your project
fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mockspresso2.MockspressoBuilder()
    .makeRealObjectsUsingPrimaryConstructor()
```

### Entry-point in single module project
It may not be feasible to dedicate a module to mockspresso support. In that case it's better to avoid duplicating the [`MockspressoBuilder()`](dokka/core/com.episode6.mockspresso2/-mockspresso-builder.html) entry-point and prefer defining a `withDefaults()` plugin for all tests to apply...
```kotlin
// test-support code
fun MockspressoBuilder.withDefaults(): MockspressoBuilder = this
    .makeRealObjectsUsingPrimaryConstructor()

// Usage example:
class MyTest {

    // use built-in entry-point but require defaults plugin as a best-practice
    val mxo = MockspressoBuilder().withDefaults()
        .build()
}
```


### Auto-Mock support

Mockspresso is agnostic to mocks and should work with any mocking framework that works with kotlin, but we do offer plugin modules to add fallback support (a.k.a. auto-mocking) with [Mockk](https://mockk.io/) (using [`plugins-mockk`](dokka/plugins-mockk/com.episode6.mockspresso2.plugins.mockk/index.html)) or [Mockito](https://site.mockito.org/) (using [`plugins-mockito`](dokka/plugins-mockito/com.episode6.mockspresso2.plugins.mockito/index.html))

```groovy
dependencies {
    api "com.episode6.mockspresso2:plugins-mockito:$mxoVersion"
    // OR
    api "com.episode6.mockspresso2:plugins-mockk:$mxoVersion"
}
```

Add auto-mock support to your `MockspressoBuilder()` entry-point
```diff
 fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mockspresso2.MockspressoBuilder()
     .makeRealObjectsUsingPrimaryConstructor()
+    .fallbackWithMockk() // or fallbackWithMockito()
```

### JSR-330 & Dagger2 support

Mockspresso provides plugin modules to support [javax.inject](https://github.com/javax-inject/javax-inject)/[dagger2](https://dagger.dev/) constructor, field & method injection using either [`plugins-javax-inject`](dokka/plugins-javax-inject/com.episode6.mockspresso2.plugins.javax.inject/index.html) and/or [`plugins-dagger2`](dokka/plugins-dagger2/com.episode6.mockspresso2.plugins.dagger2/index.html).

```groovy
dependencies {
    api "com.episode6.mockspresso2:plugins-javax-inject:$mxoVersion"
    // AND / OR
    api "com.episode6.mockspresso2:plugins-dagger2:$mxoVersion"
}
```

Instruct your default `MockspressoBuilder()` to require `@Inject` annotations on constructors and to apply field and method injection when constructing real objects.
```diff
 fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mockspresso2.MockspressoBuilder()
-    .makeRealObjectsUsingPrimaryConstructor()
+    .makeRealObjectsUsingJavaxInjectRules() // or makeRealObjectsUsingDagger2Rules()
     .fallbackWithMockk()
```
**Note:** [`Dagger2Rules`](dokka/plugins-dagger2/com.episode6.mockspresso2.plugins.dagger2/make-real-objects-using-dagger2-rules.html) is a super-set of [`JavaxInjectRules`](dokka/plugins-javax-inject/com.episode6.mockspresso2.plugins.javax.inject/make-real-objects-using-javax-inject-rules.html) that adds support for constructors with the `@AssistedInject` annotation.

The [`plugins-javax-inject`](dokka/plugins-javax-inject/com.episode6.mockspresso2.plugins.javax.inject/index.html) module also includes the [`MockspressoInstance.injectNow(Any)`](dokka/plugins-javax-inject/com.episode6.mockspresso2.plugins.javax.inject/inject-now.html) plugin to inject any pre-existing object with field / method injection. Than can be helpful in android development when running robolectric tests on Activities, Services, etc. that must be created by the system. 

#### Automatic Provider, Lazy and AssistedFactory handling

Included in the [`plugins-javax-inject`](dokka/plugins-javax-inject/com.episode6.mockspresso2.plugins.javax.inject/index.html) and [`plugins-dagger2`](dokka/plugins-dagger2/com.episode6.mockspresso2.plugins.dagger2/index.html) modules are plugins to automatically map `java.inject.Provider<T>` and `dagger.Lazy<T>` to their underlying dependencies.
```diff
 // dagger2 example
 fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mockspresso2.MockspressoBuilder()
     .makeRealObjectsUsingDagger2Rules()
+    .javaxProviderSupport()    // provided by plugins-javax-inject module
+    .dagger2LazySupport()      // provided by plugins-dagger2 module
     .fallbackWithMockk()
```

Interfaces annotated with `@dagger.AssistedFactory` can also be handled automatically using an additional plugin provided by [`com.episode6.mockspresso2:plugins-mockito-factories`](dokka/plugins-mockito-factories/com.episode6.mockspresso2.plugins.mockito.factories/index.html).

```diff
 // dagger2 example
 fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mockspresso2.MockspressoBuilder()
     .makeRealObjectsUsingDagger2Rules()
     .javaxProviderSupport()
     .dagger2LazySupport()
+    .autoFactoriesByAnnotation<AssistedFactory>()  // provided by plugins-mockito-factories module
     .fallbackWithMockk()
```

**Note:** The [`plugins-mockito-factories`](dokka/plugins-mockito-factories/com.episode6.mockspresso2.plugins.mockito.factories/index.html) module requires mockito be included in the project, but does not require your tests know or care about it. It should work side-by-side with mockk but is only applicable in jvm projects.

### Integration with testing frameworks

Mockspresso is totally agnostic to testing frameworks. Ensuring and tearing down mockspresso instances isn't even required for tests (ensuring will be done lazily and teardown callbacks will simply be omitted). However it's a best practice (especially in large projects) to integrate mockspresso's life-cycle with your test-framework's to ensure setup and teardown callbacks all fire at expected times. 

We currently offer support plugins [`plugins-junit4`](dokka/plugins-junit4/com.episode6.mockspresso2.plugins.junit4/index.html) and [`plugins-junit5`](dokka/plugins-junit5/com.episode6.mockspresso2.plugins.junit5/index.html) to assist with this.
```groovy
dependencies {
    api "com.episode6.mockspresso2:plugins-junit4:$mxoVersion"
    // OR
    api "com.episode6.mockspresso2:plugins-junit5:$mxoVersion"
}
```

JUnit 4 Rule
```kotlin
@get:Rule val mxo = BuildMockspresso().build()
    .junitRule() // returns an object that implements both 
                 // Mockspresso and ClassRule interfaces
```

JUnit 5 Extension
```kotlin
@RegisterExtension val mxo = BuildMockspresso().build()
    .junitExtension() // returns an object that implements both 
                      // Mockspresso and Extension interfaces
```

### Get started writing tests
See the [Getting Started](GETTING_STARTED.md) doc to start writing tests.
