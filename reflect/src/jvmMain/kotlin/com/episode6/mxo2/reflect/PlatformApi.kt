package com.episode6.mxo2.reflect

import javax.inject.Qualifier
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.hasAnnotation

actual fun Annotation.isQualifier(): Boolean = annotationClass.hasAnnotation<Qualifier>()

actual fun KFunction<*>.parameterKeys(context: TypeToken<*>): List<DependencyKey<*>> =
  parameters.map { it.toDependencyKey(context) }

private fun KParameter.toDependencyKey(context: TypeToken<*>): DependencyKey<*> = DependencyKey(
  token = context.resolveType(type),
  qualifier = annotations.findQualifier { "param $this in class $context" }
)
