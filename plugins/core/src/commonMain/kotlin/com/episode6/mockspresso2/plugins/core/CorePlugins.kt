package com.episode6.mockspresso2.plugins.core

import com.episode6.mockspresso2.MockspressoProperties
import com.episode6.mockspresso2.plugins.core.reflect.reflectionRealObjectMaker
import com.episode6.mockspresso2.reflect.allConstructors
import com.episode6.mockspresso2.reflect.asKClass
import com.episode6.mockspresso2.reflect.parameterCount
import com.episode6.mockspresso2.reflect.primaryConstructor

/**
 * Instruct this mockspresso instance to create real objects by calling their primary constructor.
 */
fun MockspressoProperties.makeRealObjectsUsingPrimaryConstructor() {
  makeRealObjectsWith(reflectionRealObjectMaker { token.asKClass().primaryConstructor() })
}

/**
 * Instruct this mockspresso instance to create real objects by calling the constructor with the fewest parameters.
 */
fun MockspressoProperties.makeRealObjectsUsingShortestConstructor() {
  makeRealObjectsWith(
    reflectionRealObjectMaker { token.asKClass().allConstructors().minByOrNull { f -> f.parameterCount() }!! }
  )
}

/**
 * Instruct this mockspresso instance to create real objects by calling the constructor with the most parameters.
 */
fun MockspressoProperties.makeRealObjectsUsingLongestConstructor() {
  makeRealObjectsWith(
    reflectionRealObjectMaker { token.asKClass().allConstructors().maxByOrNull { f -> f.parameterCount() }!! }
  )
}
