package com.episode6.mxo2.plugins.javax.inject

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.plugins.core.reflectionRealObjectMaker
import com.episode6.mxo2.reflect.*
import javax.inject.Inject
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

fun MockspressoBuilder.makeRealObjectsUsingJavaxInjectRules(): MockspressoBuilder {
  val reflectMaker = reflectionRealObjectMaker { findInjectConstructor() }

  return makeRealObjectsWith { key, dependencies ->
    reflectMaker.makeObject(key, dependencies).also { obj ->
      key.findInjectMemberProperties().forEach {
        it.tryMakeAccessible()
        val param = dependencies.get(key.resolveKeyForCallableReturnType(it))
        it.setter.call(param)
      }
      key.findInjectMemberFunctions().forEach { function ->
        function.tryMakeAccessible()
        val params = function.parameterKeys(context = key.token).map { dependencies.get(it) }
        function.call(*params.toTypedArray())
      }
    }
  }
}

private fun DependencyKey<*>.resolveKeyForCallableReturnType(callable: KCallable<*>): DependencyKey<*> = DependencyKey(
  token = token.resolveType(callable.returnType),
  qualifier = callable.annotations.findQualifier { "member ${callable.name} in class $this" }
)

private fun DependencyKey<*>.findInjectConstructor(): KFunction<*> {
  val injectConstructors = token.asKClass().allConstructors().filter { it.hasAnnotation<Inject>() }
  return when {
    injectConstructors.isEmpty() -> throw NoInjectConstructorsException(this)
    injectConstructors.size > 1  -> throw MultipleInjectConstructorsException(this)
    else                         -> injectConstructors.first()
  }
}

private fun DependencyKey<*>.findInjectMemberProperties() = token.asKClass()
  .memberProperties
  .filter { it.hasAnnotation<Inject>() }
  .filterIsInstance<KMutableProperty<Any?>>()

private fun DependencyKey<*>.findInjectMemberFunctions() = token.asKClass()
  .memberFunctions
  .filter { it.hasAnnotation<Inject>() }

class MultipleInjectConstructorsException(key: DependencyKey<*>) :
  AssertionError("Multiple constructors found with @Inject annotation. Only one is allowed. Key: $key")

class NoInjectConstructorsException(key: DependencyKey<*>) :
  AssertionError("No constructors found that apply @Inject annotation. Key: $key")
