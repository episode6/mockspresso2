package com.episode6.mockspresso2.plugins.javax.inject

import com.episode6.mockspresso2.MockspressoInstance
import com.episode6.mockspresso2.MockspressoProperties
import com.episode6.mockspresso2.plugins.javax.inject.reflect.asDependencies
import com.episode6.mockspresso2.plugins.javax.inject.reflect.injectWithDependencies
import com.episode6.mockspresso2.plugins.javax.inject.reflect.javaxRealObjectMaker
import com.episode6.mockspresso2.reflect.TypeToken
import kotlin.reflect.full.createType

/**
 * Make real objects using reflection according to java.inject rules. Supports constructor, property, field and
 * method injection.
 */
fun MockspressoProperties.makeRealObjectsUsingJavaxInjectRules() {
  makeRealObjectsWith(javaxRealObjectMaker())
}

/**
 * Enable automatic mapping of [javax.inject.Provider]s to their underlying dependencies.
 */
fun MockspressoProperties.javaxProviderSupport() {
  addDynamicObjectMaker(javaxProviderMaker())
}

/**
 * Perform field and method injection on an object that's been created outside the context of mockspresso.
 *
 * WARNING: this method will throw an [IllegalArgumentException] immediately if [instance] is not a concrete class.
 * To inject generics use the alternate signature of [injectNow] that accepts a [TypeToken]
 */
fun MockspressoInstance.injectNow(instance: Any) {
  injectNow(instance, TypeToken(instance.javaClass.kotlin.createType()))
}

/**
 * Perform field and method injection on an object that's been created outside the context of mockspresso. This method
 * is safe to use with generic objects
 */
fun <T : Any> MockspressoInstance.injectNow(instance: T, token: TypeToken<T>) {
  instance.injectWithDependencies(token, asDependencies())
}
