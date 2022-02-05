package com.episode6.mxo2.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class TypeToken<T : Any?>(val type: KType)
data class DependencyKey<T : Any?>(val token: TypeToken<T>, val qualifier: Annotation? = null)

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any?> typeToken(): TypeToken<T> = TypeToken(typeOf<T>())
inline fun <reified T : Any?> dependencyKey(qualifier: Annotation? = null) = DependencyKey<T>(typeToken(), qualifier)
fun TypeToken<*>.asKClass(): KClass<*> = type.classifier as KClass<*>
