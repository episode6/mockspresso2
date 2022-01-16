package com.episode6.mxo2.plugins.mockito

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.MockspressoProperties
import com.episode6.mxo2.addDependencyOf
import com.episode6.mxo2.api.FallbackObjectMaker
import com.episode6.mxo2.depOf
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.asKClass
import org.mockito.Incubating
import org.mockito.Mockito
import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.UseConstructor
import org.mockito.listeners.InvocationListener
import org.mockito.mock.SerializableMode
import org.mockito.stubbing.Answer
import kotlin.reflect.KClass
import org.mockito.kotlin.mock as _mock

/**
 * Use mockito to generate fallback objects for dependencies that are not present in the mockspresso instance
 */
@Suppress("UNCHECKED_CAST") fun MockspressoBuilder.fallbackWithMockito(): MockspressoBuilder =
  makeFallbackObjectsWith(object : FallbackObjectMaker {
    override fun <T> makeObject(key: DependencyKey<T>): T = Mockito.mock(key.token.asKClass().java as Class<T>)
  })

/**
 * Add a mock with the supplied params as a dependency in this mockspresso instance. Mock will be bound with the
 * supplied qualifier annotation. If you need a reference to the mock dependency, consider [MockspressoProperties.mock]
 * instead.
 */
inline fun <reified T : Any> MockspressoBuilder.defaultMock(
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
): MockspressoBuilder = addDependencyOf(qualifier) {
  _mock<T>(
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
}

/**
 * Add a mock with the supplied params as a dependency in this mockspresso instance. Mock will be bound with the
 * supplied qualifier annotation. If you need a reference to the mock dependency, consider [MockspressoProperties.mock]
 * instead.
 */
inline fun <reified T : Any> MockspressoBuilder.defaultMock(
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
): MockspressoBuilder = addDependencyOf(qualifier) {
  _mock<T>(
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
    lenient = lenient,
    stubbing = stubbing,
  )
}

/**
 * Add a mock with the supplied params as a dependency in this mockspresso instance. Mock will be bound with the
 * supplied qualifier annotation and will be accessible via the returned lazy.
 */
inline fun <reified T : Any> MockspressoProperties.mock(
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
): Lazy<T> = depOf(qualifier) {
  _mock<T>(
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
}

/**
 * Add a mock with the supplied params as a dependency in this mockspresso instance. Mock will be bound with the
 * supplied qualifier annotation and will be accessible via the returned lazy.
 */
inline fun <reified T : Any> MockspressoProperties.mock(
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
): Lazy<T> = depOf(qualifier) {
  _mock<T>(
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
    lenient = lenient,
    stubbing = stubbing,
  )
}