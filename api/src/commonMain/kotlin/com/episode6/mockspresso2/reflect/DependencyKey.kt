package com.episode6.mockspresso2.reflect

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A representation of the type [T]. Keeping this object generically typed enables us to ensure type-safety at the api
 * level. Under the hood, the generic aspect of [TypeToken] is irrelevant, only the provided [KType] matters.
 *
 * NOTE: In practice it's best to avoid using the primary constructor to create TypeTokens. Prefer the
 * [typeToken] method which uses a reified type to ensure type data is captured completely.
 */
data class TypeToken<T : Any?>(val type: KType)

/**
 * A representation of a binding "key" for Mockspresso's dependency Map. Every dependency in mockspresso is bound
 * by a [TypeToken] and an optional qualifier [Annotation].
 *
 * NOTE: In practice it's best to avoid using the primary constructor to create DependencyKeys. Prefer the
 * [dependencyKey] method which uses a reified type to ensure type data is captured completely.
 */
data class DependencyKey<T : Any?>(val token: TypeToken<T>, val qualifier: Annotation? = null)

/**
 * Creates a [TypeToken] which captures type [T]
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any?> typeToken(): TypeToken<T> = TypeToken(typeOf<T>())

/**
 * Creates a [DependencyKey] of type [T] + [qualifier]
 */
inline fun <reified T : Any?> dependencyKey(qualifier: Annotation? = null) = DependencyKey<T>(typeToken(), qualifier)
