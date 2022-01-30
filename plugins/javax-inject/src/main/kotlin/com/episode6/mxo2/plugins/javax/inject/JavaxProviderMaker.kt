package com.episode6.mxo2.plugins.javax.inject

import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.api.DynamicObjectMaker
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.TypeToken
import com.episode6.mxo2.reflect.asKClass
import javax.inject.Provider

internal fun javaxProviderMaker(): DynamicObjectMaker = DynamicObjectMaker { key, dependencies ->
  when {
    key.isGenericProvider() -> DynamicObjectMaker.Answer.Yes(dependencies.providerFor(key.typeArgumentKey()))
    else                    -> DynamicObjectMaker.Answer.No
  }
}

private fun DependencyKey<*>.isGenericProvider(): Boolean =
  token.asKClass() == Provider::class && token.type.arguments.isNotEmpty()

private fun Dependencies.providerFor(key: DependencyKey<*>): Provider<Any> = Provider { get(key) }

private fun DependencyKey<*>.typeArgumentKey(index: Int = 0): DependencyKey<*> = DependencyKey<Any>(
  token = TypeToken(token.type.arguments[index].type!!),
  qualifier = qualifier
)
