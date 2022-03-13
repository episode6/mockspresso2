package com.episode6.mockspresso2.plugins.dagger2

import com.episode6.mockspresso2.api.Dependencies
import com.episode6.mockspresso2.api.DynamicObjectMaker
import com.episode6.mockspresso2.reflect.DependencyKey
import com.episode6.mockspresso2.reflect.TypeToken
import com.episode6.mockspresso2.reflect.asKClass

internal fun dagger2LazyMaker(): DynamicObjectMaker = DynamicObjectMaker { key, dependencies ->
  when {
    key.isGenericLazy() -> DynamicObjectMaker.Answer.Yes(dependencies.lazyFor(key.typeArgumentKey()))
    else                -> DynamicObjectMaker.Answer.No
  }
}

private fun DependencyKey<*>.isGenericLazy(): Boolean =
  token.asKClass() == dagger.Lazy::class && token.type.arguments.isNotEmpty()

private fun Dependencies.lazyFor(key: DependencyKey<*>): dagger.Lazy<Any> = dagger.Lazy { get(key) }

private fun DependencyKey<*>.typeArgumentKey(index: Int = 0): DependencyKey<*> = DependencyKey<Any>(
  token = TypeToken(token.type.arguments[index].type!!),
  qualifier = qualifier
)
