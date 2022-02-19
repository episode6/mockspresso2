## Project Setup
Ideally, mockspresso should only be exposed to tests via a dedicated gradle module. This lets us define MockspressoBuilder with defaults appropriate to our project and prevents accidental usage of the default `MockspressoBuilder()` entry-point.

### Gradle setup
```groovy
def mxoVersion = '2.0.0-alpha01-SNAPSHOT'

repositories { maven { url "https://oss.sonatype.org/content/repositories/snapshots" } }
dependencies {

    // expose the api to other modules
    api "com.episode6.mockspresso2:api:$mxoVersion"

    // hide the core module from other modules
    implementation "com.episode6.mockspresso2:core:$mxoVersion"
}
```

### Project entry point
```kotlin
package com.sample.myproject.testsupport

// define a simple custom entry-point for your project
fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mxo2.MockspressoBuilder()
    .makeRealObjectsUsingPrimaryConstructor()
```

### Mock and Auto-Mock support

Mockspresso is agnostic to mocks and should work with any mocking framework that works with kotlin, but we do offer plugin modules to add fallback support (a.k.a. auto-mocking) with [Mockk](https://mockk.io/) or [Mockito](https://site.mockito.org/)

```groovy
dependencies {
    api "com.episode6.mockspresso2:plugins-mockito:$mxoVersion"
    // OR
    api "com.episode6.mockspresso2:plugins-mockk:$mxoVersion"
}
```

Add auto-mock support to your `MockspressoBuilder()` entry-point
```diff
 fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mxo2.MockspressoBuilder()
     .makeRealObjectsUsingPrimaryConstructor()
+    .fallbackWithMockk() // or fallbackWithMockito()
```

### Inject & Dagger support

Mockspresso provides plugin modules to support javax.inject constructor, field & method injection.

```groovy
dependencies {
    api "com.episode6.mockspresso2:plugins-javax-inject:$mxoVersion"
    // AND / OR
    api "com.episode6.mockspresso2:plugins-dagger2:$mxoVersion"
}
```

Instruct your default `MockspressoBuilder()` to require `@Inject` annotations on constructors and to apply field and method injection when constructing real objects.
```diff
 fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mxo2.MockspressoBuilder()
-    .makeRealObjectsUsingPrimaryConstructor()
+    .makeRealObjectsUsingJavaxInjectRules() // or makeRealObjectsUsingDagger2Rules()
     .fallbackWithMockk()
```
**Note:** `Dagger2Rules` is a super-set of `JavaxInjectRules` that adds support for constructors with the `@AssistedInject` annotation.

The `plugins-javax-inject` also includes a plugin to allow a `MockspressoInstance` to inject any pre-existing object with field / method injection. Than can be helpful in android development when running robolectric tests on Activities, Services, etc. that must be created by the system. example:

```kotlin
val mxo = BuildMockspresso().build()

@Test fun testWelcomeActivity() {
  val activity = Robolectric.setupActivity(WelcomeActivity::class.java)
  mxo.inject(activity) // it's also possible to set this up automatically using a custom Application
}
```

#### Automatic Provider, Lazy and AssistedFactory handling

Included in the `javax-inject` and `dagger2` modules are plugins to automatically map `java.inject.Provider<T>` and `dagger.Lazy<T>` to their underlying dependencies.
```diff
 // dagger2 example
 fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mxo2.MockspressoBuilder()
     .makeRealObjectsUsingDagger2Rules()
+    .javaxProviderSupport()
+    .dagger2LazySupport()
     .fallbackWithMockk()
```

Interfaces annotated with `@dagger.AssistedFactory` can also be handled automatically using an additional plugin provided by `com.episode6.mockspresso2:plugins-mockito-factories`.

```diff
 // dagger2 example
 fun MockspressoBuilder(): MockspressoBuilder = com.episode6.mxo2.MockspressoBuilder()
     .makeRealObjectsUsingDagger2Rules()
     .javaxProviderSupport()
     .dagger2LazySupport()
+    .autoFactoriesByAnnotation<AssistedFactory>()
     .fallbackWithMockk()
```

**Note:** The `plugins-mockito-factories` requires mockito be included in the project, but does not require your tests know or care about it. It should work side-by-side with mockk (in jvm projects).

### Integration with testing frameworks

Mockspresso is totally agnostic to testing frameworks. Ensuring and tearing down mockspresso instances isn't even required for tests (ensuring will be done lazily and teardown callbacks will simply be omitted). However it's a best practice (especially in large projects) to integrate mockspresso's life-cycle with your test-framework's to ensure setup and teardown callbacks all fire at expected times. 

We currently offer support plugins for JUnit 4 & 5 to assist with this.
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
