package com.episode6.mockspresso2.plugins.javax.inject

import com.episode6.mockspresso2.api.Dependencies
import com.episode6.mockspresso2.api.DynamicObjectMaker
import com.episode6.mockspresso2.api.DynamicObjectMaker.Answer.No
import com.episode6.mockspresso2.api.DynamicObjectMaker.Answer.Yes
import com.episode6.mockspresso2.reflect.DependencyKey
import com.episode6.mockspresso2.reflect.TypeToken
import com.episode6.mockspresso2.reflect.asKClass
import javax.inject.Provider

internal fun javaxProviderMaker(): DynamicObjectMaker = DynamicObjectMaker { key, dependencies ->
  when {
    key.isGenericProvider() -> Yes(dependencies.providerFor(key.typeArgumentKey()))
    else                    -> No
  }
}

private fun DependencyKey<*>.isGenericProvider(): Boolean =
  token.asKClass() == Provider::class && token.type.arguments.isNotEmpty()

private fun Dependencies.providerFor(key: DependencyKey<*>): Provider<Any> = Provider { get(key) }

private fun DependencyKey<*>.typeArgumentKey(index: Int = 0): DependencyKey<*> = DependencyKey<Any>(
  token = TypeToken(token.type.arguments[index].type!!),
  qualifier = qualifier
)
