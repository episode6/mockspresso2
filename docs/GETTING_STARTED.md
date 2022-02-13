## Project Setup
Ideally, mockspresso should only be exposed to tests via a dedicated gradle module. This lets us define MockspressoBuilder with defaults appropriate to our project and prevents accidental usage of the default `MockspressoBuilder()` entry-point.

```groovy
def mxoVersion = '0.2.0-alpha01-SNAPSHOT'

repositories { maven { url "https://oss.sonatype.org/content/repositories/snapshots/" } }
dependencies {

    // expose the api to other modules
    api "com.episode6.mockspresso2:api:$mxoVersion"

    // hide the core module from other modules
    implementation "com.episode6.mockspresso2:core:$mxoVersion"
}
```

#### Mock support modules

```groovy
// pick a support module for your mocking framework of choice
api "com.episode6.mockspresso2:plugins-mockito:$mxoVersion"
api "com.episode6.mockspresso2:plugins-mockk:$mxoVersion"

// optionally include plugins for popular 3rd party libs/frameworks
implementation "com.episode6.mockspresso2:plugins-dagger:$mxoVersion"
implementation "com.episode6.mockspresso2:plugins-javax-inject:$mxoVersion"

// optionally include plugins for your test framework
api "com.episode6.mockspresso2:plugins-junit4:$mxoVersion"
api "com.episode6.mockspresso2:plugins-junit5:$mxoVersion"

// other utility plugins include
api "com.episode6.mockspresso2:plugins-mockito-factories:$mxoVersion"
```
