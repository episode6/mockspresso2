package com.episode6.mxo2.plugins.javax.inject

import com.episode6.mxo2.api.ObjectMaker
import com.episode6.mxo2.plugins.core.reflectionRealObjectMaker
import com.episode6.mxo2.reflect.*
import javax.inject.Inject
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

typealias AnnotationMatcher = KAnnotatedElement.() -> Boolean

private val defaultMatcher: AnnotationMatcher = { hasAnnotation<Inject>() }

fun javaxRealObjectMaker(
  chooseConstructor: DependencyKey<*>.() -> KFunction<*> = { findExactlyOneInjectConstructor(defaultMatcher) },
  isInjectProperty: AnnotationMatcher = defaultMatcher,
  isInjectFunction: AnnotationMatcher = defaultMatcher,
): ObjectMaker {
  val reflectMaker = reflectionRealObjectMaker(chooseConstructor)
  return ObjectMaker { key, dependencies ->
    // use a [reflectionRealObjectMaker] to create the objects, then inject members before it's returned
    reflectMaker.makeObject(key, dependencies).also { obj ->

      // inject properties
      key.token.asKClass().memberProperties.filter { it.isInjectProperty() }
        .filterIsInstance<KMutableProperty<Any?>>()
        .forEach {
          it.tryMakeAccessible()
          val param = dependencies.get(key.resolveKeyForCallableReturnType(it))
          it.setter.callWith(obj, param)
        }

      // inject methods
      key.token.asKClass().memberFunctions.filter { it.isInjectFunction() }.forEach { function ->
        function.tryMakeAccessible()
        val params = function.parameterKeys(context = key.token).map { dependencies.get(it) }
        function.callWith(obj, *params.toTypedArray())
      }
    }
  }
}

fun DependencyKey<*>.findExactlyOneInjectConstructor(isInjectConstructor: AnnotationMatcher): KFunction<*> {
  val injectConstructors = token.asKClass().allConstructors().filter { it.isInjectConstructor() }
  return when {
    injectConstructors.isEmpty() -> throw NoInjectConstructorsException(this)
    injectConstructors.size > 1  -> throw MultipleInjectConstructorsException(this)
    else                         -> injectConstructors.first()
  }
}

private fun DependencyKey<*>.resolveKeyForCallableReturnType(callable: KCallable<*>): DependencyKey<*> = DependencyKey(
  token = token.resolveType(callable.returnType),
  qualifier = callable.annotations.findQualifier { "member ${callable.name} in class $this" }
)

class MultipleInjectConstructorsException(key: DependencyKey<*>) :
  AssertionError("Multiple Inject constructors found; only one is allowed. Key: $key")

class NoInjectConstructorsException(key: DependencyKey<*>) :
  AssertionError("No Inject constructors found; one is required. Key: $key")
