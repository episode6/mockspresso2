package com.episode6.mxo2.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection

/**
 * Return the [KClass] for this [TypeToken]. Class is not typed to ensure support for TypeTokens of nullable types.
 */
fun TypeToken<*>.asKClass(): KClass<*> = type.classifier as KClass<*>

/**
 * Returns a concrete [TypeToken] representing the [referencedType] using the receiver as context.
 */
fun TypeToken<*>.resolveType(referencedType: KType): TypeToken<*> =
  when (val classifier = referencedType.classifier) {
    is KTypeParameter -> TypeToken<Any>(resolveTypeParamNamed(classifier.name))
    is KClass<*>      -> TypeToken<Any>(
      classifier.createConcreteType(
        arguments = referencedType.arguments.map { resolveTypeProjection(it) },
        nullable = referencedType.isMarkedNullable
      )
    )
    else              -> throw TypeTokenResolutionError(referencedType, context = this)
  }

private fun TypeToken<*>.resolveTypeProjection(referencedType: KTypeProjection): KTypeProjection =
  when (val classifier = referencedType.type?.classifier) {
    is KTypeParameter -> referencedType.copy(type = resolveTypeParamNamed(classifier.name))
    else              -> referencedType
  }

private fun TypeToken<*>.resolveTypeParamNamed(name: String): KType =
  type.arguments[indexOfTypeParamNamed(name)].type!!

private fun TypeToken<*>.indexOfTypeParamNamed(name: String): Int =
  asKClass().typeParameters().indexOfFirst { it.name == name }

/**
 * Thrown when an error occurs while trying to resolve a [KType] into a concrete [TypeToken]
 */
class TypeTokenResolutionError(referencedType: KType, context: TypeToken<*>) :
  AssertionError("Error resolving type $referencedType given context of $context")
