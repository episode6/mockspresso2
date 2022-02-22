package com.episode6.mxo2.plugins.mockito.factories.reflect

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.MockspressoProperties
import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.asJClass
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
fun <T : Any?> Dependencies.autoFactoryMock(factoryKey: DependencyKey<T>): T =
  Mockito.mock(factoryKey.token.asJClass(), mockitoAutoFactoryAnswer(factoryKey))

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
