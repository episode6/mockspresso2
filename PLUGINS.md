## Plugin Modules

Mockspresso ships the following plugin modules...

#### Core

 - [`com.episode6.mockspresso2:plugins-core`]({{ site.docsDir }}/plugins-core/com.episode6.mockspresso2.plugins.core/index.html)
    - Core plugins for making real objects using reflection
    - Included in [`core`]({{ site.docsDir }}/core/com.episode6.mockspresso2/index.html) module.


#### Mock Support

 - [`com.episode6.mockspresso2:plugins-mockk`]({{ site.docsDir }}/plugins-mockk/com.episode6.mockspresso2.plugins.mockk/index.html)
    - Fallback & mock support for [Mockk](https://mockk.io/)
 - [`com.episode6.mockspresso2:plugins-mockito`]({{ site.docsDir }}/plugins-mockito/com.episode6.mockspresso2.plugins.mockito/index.html)
    - Fallback & mock support for [Mockito](http://mockito.org/)


#### Test Framework Support

 - [`com.episode6.mockspresso2:plugins-junit4`]({{ site.docsDir }}/plugins-junit4/com.episode6.mockspresso2.plugins.junit4/index.html)
    - Lifecycle plugin for [JUnit 4](https://junit.org/junit4/)
 - [`com.episode6.mockspresso2:plugins-junit5`]({{ site.docsDir }}/plugins-junit5/com.episode6.mockspresso2.plugins.junit5/index.html)
    - Lifecycle plugin for [JUnit 5](https://junit.org/junit5/)


#### DI Framework Support

 - [`com.episode6.mockspresso2:plugins-javax-inject`]({{ site.docsDir }}/plugins-javax-inject/com.episode6.mockspresso2.plugins.javax.inject/index.html)
    - RealObjectMaker that supports field and method injection
    - Plugin to support automatic mapping of `Provider<T> -> T`
    - Plugin to [inject]({{ site.docsDir }}/plugins-javax-inject/com.episode6.mockspresso2.plugins.javax.inject/index.html#-227940497%2FFunctions%2F-1937516557) a pre-existing object with mockspresso dependencies
 - [`com.episode6.mockspresso2:plugins-dagger2`]({{ site.docsDir }}/plugins-dagger2/com.episode6.mockspresso2.plugins.dagger2/index.html)
    - RealObjectMaker that adds support for `dagger.@AssistedInject`
    - Plugin to support automatic mapping of `Lazy<T> -> T`


#### Misc

 - [`com.episode6.mockspresso2:plugins-mockito-factories`]({{ site.docsDir }}/plugins-mockito-factories/com.episode6.mockspresso2.plugins.mockito.factories/index.html)
    - Plugins to generate automatic factories using [Mockito](http://mockito.org/) under the hood.
