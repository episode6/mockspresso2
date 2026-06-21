package com.episode6.mockspresso2.reflect

import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

public actual fun KCallable<*>.tryMakeAccessible() { isAccessible = true }
public actual fun KClass<*>.primaryConstructor(): KFunction<*> = primaryConstructor!!
public actual fun KClass<*>.allConstructors(): List<KFunction<*>> = constructors.toList()
public actual fun KFunction<*>.parameterCount(): Int = parameters.size
public actual fun KFunction<*>.callWith(vararg args: Any?): Any? = call(*args)
public actual fun KClass<*>.typeParameters(): List<KTypeParameter> = typeParameters
public actual fun KClass<*>.createConcreteType(arguments: List<KTypeProjection>, nullable: Boolean): KType = createType(arguments, nullable)
