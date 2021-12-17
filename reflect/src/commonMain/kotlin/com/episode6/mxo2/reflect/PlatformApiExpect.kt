package com.episode6.mxo2.reflect

import kotlin.reflect.KFunction

/**
 * Returns true if the receiver is a qualifier annotation
 */
expect fun Annotation.isQualifier(): Boolean

/**
 * Returns a list of [DependencyKey]s that represent the parameters of the receiver [KFunction].
 *
 * [context] should be a concrete [TypeToken] representing the class this receiver [KFunction] is a member of.
 */
expect fun KFunction<*>.parameterKeys(context: TypeToken<*>): List<DependencyKey<*>>
