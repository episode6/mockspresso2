package com.episode6.mxo2.reflect

import kotlin.reflect.*

/**
 * Tries to make a [KFunction] accessible if it is not already
 */
expect fun KCallable<*>.tryMakeAccessible()

/**
 * Returns the primary constructor for a [KClass]
 */
expect fun KClass<*>.primaryConstructor(): KFunction<*>

/**
 * Returns a list of all constructors for a [KClass]
 */
expect fun KClass<*>.allConstructors(): List<KFunction<*>>

/**
 * Returns the number of parameters for a given [KFunction]
 */
expect fun KFunction<*>.parameterCount(): Int

/**
 * Call the receiver [KFunction] with the provided [args]
 */
expect fun KFunction<*>.callWith(vararg args: Any?): Any?

/**
 * Returns a list of type parameters for the receiver [KClass]
 */
expect fun KClass<*>.typeParameters(): List<KTypeParameter>

/**
 * Creates a concrete type of the receiver [KClass] using the provided [arguments] to fill in any type parameters.
 */
expect fun KClass<*>.createConcreteType(arguments: List<KTypeProjection>, nullable: Boolean): KType
