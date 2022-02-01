package com.episode6.mxo2.plugins.dagger2

import com.episode6.mxo2.MockspressoBuilder
import com.episode6.mxo2.plugins.javax.inject.findExactlyOneInjectConstructor
import com.episode6.mxo2.plugins.javax.inject.javaxRealObjectMaker
import dagger.assisted.AssistedInject
import javax.inject.Inject
import kotlin.reflect.full.hasAnnotation

/**
 * Make real objects using reflection according to dagger rules. Supports constructor, property, field and
 * method injection. This is the same as javax.inject rules except we also accept the [AssistedInject] as a
 * valid constructor annotation.
 */
fun MockspressoBuilder.makeRealObjectsUsingDagger2Rules(): MockspressoBuilder = makeRealObjectsWith(
  javaxRealObjectMaker { findExactlyOneInjectConstructor { hasAnnotation<Inject>() || hasAnnotation<AssistedInject>() } }
)
