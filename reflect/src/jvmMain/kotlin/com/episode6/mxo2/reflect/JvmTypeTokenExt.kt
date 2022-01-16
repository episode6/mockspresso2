package com.episode6.mxo2.reflect

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.jvmName

/**
 * Returns a concrete [TypeToken] representing the given [jvmType].
 * @param jvmType The java type to resolve
 * @param declaringClass The class the jvmType is declared on (needed to resolve type arguments)
 * @receiver The context for this look up (must implement [declaringClass])
 */
fun TypeToken<*>.resolveJvmType(jvmType: Type, declaringClass: Class<*>): TypeToken<*> =
  when (jvmType) {
    is TypeVariable<*>   -> findActualTypeOfGenericTypeArgument(declaringClass, jvmType.name)
    is ParameterizedType -> resolveParamType(jvmType, declaringClass)
    is Class<*>          -> resolveType(jvmType.kotlin.createType())
    else                 -> throw JvmTypeResolutionError("Not sure how to resolve $jvmType")
  }

private fun TypeToken<*>.resolveParamType(paramType: ParameterizedType, declaringClass: Class<*>): TypeToken<*> =
  (paramType.rawType as? Class<*>)
    ?.kotlin
    ?.createConcreteType(resolveTypeArgumentsFor(paramType, declaringClass), nullable = false)
    ?.let { TypeToken<Any>(it) }
    ?: throw JvmTypeResolutionError("Could not resolve paramType: $paramType in declaringClass: $declaringClass using context: $this")

private fun TypeToken<*>.resolveTypeArgumentsFor(paramType: ParameterizedType, declaringClass: Class<*>) = paramType
  .actualTypeArguments
  .map { KTypeProjection(KVariance.INVARIANT, resolveJvmType(it, declaringClass).type) }

private fun TypeToken<*>.findActualTypeOfGenericTypeArgument(
  declaringClass: Class<*>,
  paramName: String
): TypeToken<*> {
  if (type matches declaringClass) {
    val typeArgumentIndex = findIndexForTypeArgument(declaringClass, paramName)
    return TypeToken<Any>(type.arguments[typeArgumentIndex].type!!)
  }

  return asKClass().allSupertypes
    .first { it matches declaringClass }
    .let { TypeToken<Any>(it) }
    .findActualTypeOfGenericTypeArgument(declaringClass, paramName)
    .let { resolveType(it.type) }
}

private fun findIndexForTypeArgument(clazz: Class<*>, typeParamName: String): Int = clazz.typeParameters
  .indexOfFirst { it.name == typeParamName }
  .takeUnless { it < 0 }
  ?: throw JvmTypeResolutionError("Could not find type param named $typeParamName on class $clazz")

private infix fun KType.matches(clazz: Class<*>): Boolean = (classifier as? KClass<*>)?.jvmName == clazz.name

class JvmTypeResolutionError(message: String) : AssertionError(message)
