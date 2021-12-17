package com.episode6.mxo2.reflect

import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

actual fun KFunction<*>.tryMakeAccessible() { isAccessible = true }
actual fun KClass<*>.primaryConstructor(): KFunction<*> = primaryConstructor!!
actual fun KClass<*>.allConstructors(): List<KFunction<*>> = constructors.toList()
actual fun KFunction<*>.parameterCount(): Int = parameters.size
actual fun KFunction<*>.callWith(vararg args: Any?): Any? = call(*args)
actual fun KClass<*>.typeParameters(): List<KTypeParameter> = typeParameters
actual fun KClass<*>.createConcreteType(arguments: List<KTypeProjection>, nullable: Boolean): KType = createType(arguments, nullable)
