package com.episode6.mxo2.reflect

import kotlin.reflect.*

// platform core
expect fun KFunction<*>.tryMakeAccessible()
expect fun KClass<*>.primaryConstructor(): KFunction<*>
expect fun KClass<*>.allConstructors(): List<KFunction<*>>
expect fun KFunction<*>.parameterCount(): Int
expect fun KFunction<*>.callWith(vararg args: Any?): Any?
expect fun KClass<*>.typeParameters(): List<KTypeParameter>
expect fun KClass<*>.createConcreteType(arguments: List<KTypeProjection>, nullable: Boolean): KType

// platform api
expect fun Annotation.isQualifier(): Boolean
expect fun KFunction<*>.parameterKeys(context: TypeToken<*>): List<DependencyKey<*>>
