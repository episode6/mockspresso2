package com.episode6.mxo2.plugins.javax.inject

import com.episode6.mxo2.MockspressoInstance
import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.api.RealObjectMaker
import com.episode6.mxo2.plugins.core.reflectionRealObjectMaker
import com.episode6.mxo2.reflect.*
import java.lang.reflect.AnnotatedElement
import javax.inject.Inject
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

typealias KAnnotationMatcher = KAnnotatedElement.() -> Boolean
typealias JAnnotationMatcher = AnnotatedElement.() -> Boolean

private val defaultKMatcher: KAnnotationMatcher = { hasAnnotation<Inject>() }
private val defaultJMatcher: JAnnotationMatcher = { isAnnotationPresent(Inject::class.java) }

/**
 * Returns an [RealObjectMaker] that uses reflection to create real objects according to javax inject rules.
 * Objects must have a single Injectable constructor and supports field/property and method injection.
 */
fun javaxRealObjectMaker(
  isInjectProperty: KAnnotationMatcher = defaultKMatcher,
  isInjectField: JAnnotationMatcher = defaultJMatcher,
  isInjectFunction: KAnnotationMatcher = defaultKMatcher,
  chooseConstructor: DependencyKey<*>.() -> KFunction<*> = { findExactlyOneInjectConstructor() },
): RealObjectMaker {
  val reflectMaker = reflectionRealObjectMaker(chooseConstructor)
  return RealObjectMaker { objKey, dependencies ->
    reflectMaker.makeRealObject(objKey, dependencies)?.also { obj ->
      obj.injectWithDependencies(objKey.token, dependencies, isInjectProperty, isInjectField, isInjectFunction)
    }
  }
}

fun DependencyKey<*>.findExactlyOneInjectConstructor(isInjectConstructor: KAnnotationMatcher = defaultKMatcher): KFunction<*> {
  val injectConstructors = token.asKClass().allConstructors().filter { it.isInjectConstructor() }
  return when {
    injectConstructors.isEmpty() -> throw NoInjectConstructorsException(this)
    injectConstructors.size > 1  -> throw MultipleInjectConstructorsException(this)
    else                         -> injectConstructors.first()
  }
}

internal fun Any.injectWithDependencies(
  token: TypeToken<*>,
  dependencies: Dependencies,
  isInjectProperty: KAnnotationMatcher = defaultKMatcher,
  isInjectField: JAnnotationMatcher = defaultJMatcher,
  isInjectFunction: KAnnotationMatcher = defaultKMatcher,
) {
  token.asKClass().memberProperties.filterIsInstance<KMutableProperty<*>>()
    .filter { it.setter.isInjectProperty() || it.javaField?.isInjectField() == true }
    .forEach { property ->
      property.tryMakeAccessible()
      val param = dependencies.get(property.returnTypeKey(context = token))
      property.setter.callWith(this, param)
    }
  token.asKClass().memberFunctions.filter { it.isInjectFunction() }
    .forEach { function ->
      function.tryMakeAccessible()
      val params = function.parameterKeys(context = token).drop(1).map { dependencies.get(it) }
      function.callWith(this, *params.toTypedArray())
    }
}

internal fun MockspressoInstance.asDependencies(): Dependencies = object : Dependencies {
  override fun <T> get(key: DependencyKey<T>): T = findDependency(key)
}

private fun KCallable<*>.returnTypeKey(context: TypeToken<*>): DependencyKey<*> = DependencyKey(
  token = context.resolveType(returnType),
  qualifier = annotations.findQualifier { "member $name in class $this" }
)

class MultipleInjectConstructorsException(key: DependencyKey<*>) :
  AssertionError("Multiple Inject constructors found; only one is allowed. Key: $key")

class NoInjectConstructorsException(key: DependencyKey<*>) :
  AssertionError("No Inject constructors found; one is required. Key: $key")
