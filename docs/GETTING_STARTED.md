## Project Setup
```groovy
def mxoVersion = '0.2.0-alpha01-SNAPSHOT'

repositories { maven { url "https://oss.sonatype.org/content/repositories/snapshots/" } }
dependencies {

    // In a multi-module project it can be helpful to expose the api w/o exposing the entry-point in core
    api "com.episode6.mockspresso2:api:$mxoVersion"

    // core module is required
    implementation "com.episode6.mockspresso2:core:$mxoVersion"

    // pick a support module for your mocking framework of choice
    implementation "com.episode6.mockspresso2:plugins-mockito:$mxoVersion"
    implementation "com.episode6.mockspresso2:plugins-mockk:$mxoVersion"

    // optionally include plugins for popular 3rd party libs
    implementation "com.episode6.mockspresso2:plugins-dagger:$mxoVersion"
    implementation "com.episode6.mockspresso2:plugins-javax-inject:$mxoVersion"

    // optionally include plugins for your test framework
    implementation "com.episode6.mockspresso2:plugins-junit4:$mxoVersion"
    implementation "com.episode6.mockspresso2:plugins-junit5:$mxoVersion"

    // other utility plugins include
    implementation "com.episode6.mockspresso2:plugins-mockito-factories:$mxoVersion"
}
```
