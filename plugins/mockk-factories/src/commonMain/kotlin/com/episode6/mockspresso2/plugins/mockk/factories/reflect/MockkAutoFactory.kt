package com.episode6.mockspresso2.plugins.mockk.factories.reflect

import com.episode6.mockspresso2.MockspressoBuilder
import com.episode6.mockspresso2.MockspressoProperties
import com.episode6.mockspresso2.api.Dependencies
import com.episode6.mockspresso2.reflect.*
import io.mockk.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

/**
 * Returns a Factory object for the given [factoryKey]. The object will be mocked
 * and each method will return a dependency from the underlying Mockspresso instance.
 *
 * Generally you shouldn't need to access this method directly, prefer applying with [MockspressoBuilder.autoFactory]
 * or [MockspressoProperties.autoFactory]
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> Dependencies.autoFactoryMock(factoryKey: DependencyKey<T>): T =
  (MockK.useImpl {
    MockKGateway.implementation().mockFactory.mockk(
      mockType = factoryKey.token.asKClass(),
      name = "autoFactoryMock:$factoryKey",
      relaxed = true,
      moreInterfaces = emptyArray(),
      relaxUnitFun = true,
    )
  } as T).also { factory ->
    factoryKey.token.asKClass().memberFunctions
      .filter { !it.isSuspend }
      .filter { it.returnType != Unit::class }
      .forEach { func ->
        every {
          val params: List<Any?> = (1 until func.parameterCount()).map { i -> reflectiveAny(func.parameters[i].type) }
          func.callWith(*(listOf<Any?>(factory) + params).toTypedArray())
        } answers {
          get(DependencyKey(TypeToken(func.returnType), factoryKey.qualifier))
        }
      }
  }

@Suppress("UNCHECKED_CAST")
private fun MockKMatcherScope.reflectiveAny(type: KType): Any =
  findCallRecorder().matcher(ConstantMatcher<Any>(true), type.classifier as KClass<Any>)

private fun MockKMatcherScope.findCallRecorder(): MockKGateway.CallRecorder {
  val func =
    MockKMatcherScope::class.memberProperties.find { it.returnType.classifier == MockKGateway.CallRecorder::class }
  return func!!.call(this) as MockKGateway.CallRecorder
}
/**
 * Returns a mockito default [Answer] for use in a mock of the given [factoryKey]. The answer will resolve the return
 * type of the called method at runtime and return a dependency from the mockspresso graph.
 */
//fun Dependencies.mockitoAutoFactoryAnswer(factoryKey: DependencyKey<*>): Answer<Any> = Answer<Any> { invoc ->
//  when (invoc.method.returnType) {
//    Void.TYPE -> null
//    else      -> factoryKey.token
//      .resolveJvmType(invoc.method.genericReturnType, invoc.method.declaringClass)
//      .let { get(DependencyKey(it, factoryKey.qualifier)) }
//  }
//}
