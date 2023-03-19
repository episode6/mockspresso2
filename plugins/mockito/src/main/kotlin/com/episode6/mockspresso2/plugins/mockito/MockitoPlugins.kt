package com.episode6.mockspresso2.plugins.mockito

import com.episode6.mockspresso2.MockspressoProperties
import com.episode6.mockspresso2.api.FallbackObjectMaker
import com.episode6.mockspresso2.dependency
import com.episode6.mockspresso2.reflect.DependencyKey
import com.episode6.mockspresso2.reflect.asJClass
import com.episode6.mockspresso2.reflect.dependencyKey
import com.episode6.mockspresso2.reflect.typeToken
import org.mockito.Incubating
import org.mockito.Mockito
import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.UseConstructor
import org.mockito.kotlin.spy
import org.mockito.kotlin.withSettings
import org.mockito.listeners.InvocationListener
import org.mockito.mock.SerializableMode
import org.mockito.stubbing.Answer
import kotlin.reflect.KClass

/**
 * Use mockito to generate fallback objects for dependencies that are not present in the mockspresso instance
 */
fun MockspressoProperties.fallbackWithMockito() {
  makeFallbackObjectsWith(object : FallbackObjectMaker {
    override fun <T> makeObject(key: DependencyKey<T>): T = Mockito.mock(key.token.asJClass())
  })
}

/**
 * Add a mock with the supplied params as a dependency in this mockspresso instance. Mock will be bound with the
 * supplied qualifier annotation and will be accessible via the returned lazy.
 */
inline fun <reified T : Any?> MockspressoProperties.mock(
  qualifier: Annotation? = null,
  extraInterfaces: Array<out KClass<out Any>>? = null,
  name: String? = null,
  spiedInstance: Any? = null,
  defaultAnswer: Answer<Any>? = null,
  serializable: Boolean = false,
  serializableMode: SerializableMode? = null,
  verboseLogging: Boolean = false,
  invocationListeners: Array<InvocationListener>? = null,
  stubOnly: Boolean = false,
  @Incubating useConstructor: UseConstructor? = null,
  @Incubating outerInstance: Any? = null,
  @Incubating lenient: Boolean = false
): Lazy<T> = dependency(qualifier) {
  Mockito.mock(
    T::class.java,
    withSettings(
      extraInterfaces = extraInterfaces,
      name = name,
      spiedInstance = spiedInstance,
      defaultAnswer = defaultAnswer,
      serializable = serializable,
      serializableMode = serializableMode,
      verboseLogging = verboseLogging,
      invocationListeners = invocationListeners,
      stubOnly = stubOnly,
      useConstructor = useConstructor,
      outerInstance = outerInstance,
      lenient = lenient
    )
  )!!
}

/**
 * Add a mock with the supplied params as a dependency in this mockspresso instance. Mock will be bound with the
 * supplied qualifier annotation and will be accessible via the returned lazy.
 */
inline fun <reified T : Any?> MockspressoProperties.mock(
  qualifier: Annotation? = null,
  extraInterfaces: Array<out KClass<out Any>>? = null,
  name: String? = null,
  spiedInstance: Any? = null,
  defaultAnswer: Answer<Any>? = null,
  serializable: Boolean = false,
  serializableMode: SerializableMode? = null,
  verboseLogging: Boolean = false,
  invocationListeners: Array<InvocationListener>? = null,
  stubOnly: Boolean = false,
  @Incubating useConstructor: UseConstructor? = null,
  @Incubating outerInstance: Any? = null,
  @Incubating lenient: Boolean = false,
  noinline stubbing: KStubbing<T>.(T) -> Unit
): Lazy<T> = dependency(qualifier) {
  Mockito.mock(
    T::class.java,
    withSettings(
      extraInterfaces = extraInterfaces,
      name = name,
      spiedInstance = spiedInstance,
      defaultAnswer = defaultAnswer,
      serializable = serializable,
      serializableMode = serializableMode,
      verboseLogging = verboseLogging,
      invocationListeners = invocationListeners,
      stubOnly = stubOnly,
      useConstructor = useConstructor,
      outerInstance = outerInstance,
      lenient = lenient
    )
  ).apply { KStubbing(this).stubbing(this) }!!
}

/**
 * Create a real object of [T] using mockspresso then wrap it in a mockito [spy]. This spy will be part of the mockspresso
 * graph and can be used by other real objects (and then verified in test code).
 */
inline fun <reified T : Any?> MockspressoProperties.spy(qualifier: Annotation? = null): Lazy<T> =
  interceptRealImplementation(dependencyKey<T>(qualifier), typeToken<T>()) { spy(it) }

/**
 * Create a real object of [T] using mockspresso then wrap it in a mockito [spy]. This spy will be part of the mockspresso
 * graph and can be used by other real objects (and then verified in test code). The [stubbing] will be applied to the
 * spy before it is injected as a dependency into other classes.
 */
inline fun <reified T : Any?> MockspressoProperties.spy(
  qualifier: Annotation? = null,
  noinline stubbing: KStubbing<T>.(T) -> Unit
): Lazy<T> = interceptRealImplementation(dependencyKey<T>(qualifier), typeToken<T>()) { spy(it, stubbing) }

/**
 * Create a real object of type [IMPL] using mockspresso then wrap it in a mockito [spy] (the object will be bound
 * using type [BIND]). This spy will be part of the mockspresso graph and can be used by other real objects
 * (and then verified in test code).
 */
inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoProperties.spyImplOf(qualifier: Annotation? = null): Lazy<IMPL> =
  interceptRealImplementation(dependencyKey<BIND>(qualifier), typeToken<IMPL>()) { spy(it) }

/**
 * Create a real object of type [IMPL] using mockspresso then wrap it in a mockito [spy] (the object will be bound
 * using type [BIND]). This spy will be part of the mockspresso graph and can be used by other real objects
 * (and then verified in test code). The [stubbing] will be applied to the spy before it is injected as a dependency
 * into other classes.
 */
inline fun <reified BIND : Any?, reified IMPL : BIND> MockspressoProperties.spyImplOf(
  qualifier: Annotation? = null,
  noinline stubbing: KStubbing<IMPL>.(IMPL) -> Unit
): Lazy<IMPL> = interceptRealImplementation(dependencyKey<BIND>(qualifier), typeToken<IMPL>()) { spy(it, stubbing) }
