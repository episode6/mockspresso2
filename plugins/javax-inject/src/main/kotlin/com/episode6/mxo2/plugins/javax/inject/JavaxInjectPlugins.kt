package com.episode6.mxo2.plugins.javax.inject

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.MockspressoInstance
import com.episode6.mxo2.api.Dependencies
import com.episode6.mxo2.reflect.DependencyKey
import com.episode6.mxo2.reflect.TypeToken

/**
 * Make real objects using reflection according to java.inject rules. Supports constructor, property, field and
 * method injection.
 */
fun MockspressoBuilder.makeRealObjectsUsingJavaxInjectRules(): MockspressoBuilder =
  makeRealObjectsWith(javaxRealObjectMaker())

/**
 * Enable automatic mapping of [javax.inject.Provider]s to their underlying dependencies.
 */
fun MockspressoBuilder.javaxProviderSupport(): MockspressoBuilder = addDynamicObjectMaker(javaxProviderMaker())

fun MockspressoInstance.inject(obj: Any) {
  val key = DependencyKey(TypeToken())
  obj.injectProperties(obj)
}
