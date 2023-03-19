package com.episode6.mockspresso2.plugins.dagger2

import com.episode6.mockspresso2.MockspressoProperties
import com.episode6.mockspresso2.plugins.javax.inject.reflect.findExactlyOneInjectConstructor
import com.episode6.mockspresso2.plugins.javax.inject.reflect.javaxRealObjectMaker
import dagger.assisted.AssistedInject
import javax.inject.Inject
import kotlin.reflect.full.hasAnnotation

/**
 * Make real objects using reflection according to dagger rules. Supports constructor, property, field and
 * method injection. This is the same as javax.inject rules except we also accept the [AssistedInject] as a
 * valid constructor annotation.
 */
fun MockspressoProperties.makeRealObjectsUsingDagger2Rules() {
  makeRealObjectsWith(
    javaxRealObjectMaker { findExactlyOneInjectConstructor { hasAnnotation<Inject>() || hasAnnotation<AssistedInject>() } }
  )
}

/**
 * Enable automatic mapping of [dagger.Lazy]s to their underlying dependencies.
 */
fun MockspressoProperties.dagger2LazySupport() {
  addDynamicObjectMaker(dagger2LazyMaker())
}
