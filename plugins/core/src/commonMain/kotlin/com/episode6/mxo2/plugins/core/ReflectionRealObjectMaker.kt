package com.episode6.mxo2.plugins.core

import com.episode6.mxo2.api.RealObjectMaker
import com.episode6.mxo2.reflect.*
import kotlin.reflect.KFunction

/**
 * Return an [RealObjectMaker] that uses reflection to create real objects which are supplied objects from
 * [com.episode6.mxo2.api.Dependencies] to satisfy their constructor parameters.
 */
fun reflectionRealObjectMaker(
  chooseConstructor: DependencyKey<*>.() -> KFunction<*> = { token.asKClass().primaryConstructor() }
): RealObjectMaker = RealObjectMaker { key, dependencies ->
  key.chooseConstructor().run {
    tryMakeAccessible()
    val params = parameterKeys(context = key.token).map { dependencies.get(it) }
    callWith(*params.toTypedArray())
  }
}
