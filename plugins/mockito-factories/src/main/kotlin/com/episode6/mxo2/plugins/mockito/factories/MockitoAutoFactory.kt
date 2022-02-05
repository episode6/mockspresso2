package com.episode6.mxo2.plugins.mockito.factories

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.MockspressoProperties
import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.dependencyKey
import com.episode6.mxo2.reflect.resolveJvmType
import org.mockito.Mockito
import org.mockito.stubbing.Answer

/**
 * Returns a Factory object for the given [factoryKey]. The object will be mocked
 * and each method will return a dependency from the underlying Mockspresso instance.
 *
 * Generally you shouldn't need to access this method directly, prefer applying with [MockspressoBuilder.autoFactory]
 * or [MockspressoProperties.autoFactory]
 */
inline fun <reified T : Any?> Dependencies.autoFactoryMock(qualifier: Annotation? = null): T =
  Mockito.mock(T::class.java, mockitoAutoFactoryAnswer(dependencyKey<T>(qualifier)))

/**
 * Returns a mockito default [Answer] for use in a mock of the given [factoryKey]. The answer will resolve the return
 * type of the called method at runtime and return a dependency from the mockspresso graph.
 */
fun Dependencies.mockitoAutoFactoryAnswer(factoryKey: DependencyKey<*>): Answer<Any> = Answer<Any> { invoc ->
  when (invoc.method.returnType) {
    Void.TYPE -> null
    else      -> factoryKey.token
      .resolveJvmType(invoc.method.genericReturnType, invoc.method.declaringClass)
      .let { get(DependencyKey(it, factoryKey.qualifier)) }
  }
}
