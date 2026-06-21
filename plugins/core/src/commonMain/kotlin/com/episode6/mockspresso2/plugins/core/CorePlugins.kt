package com.episode6.mockspresso2.plugins.core

import com.episode6.mockspresso2.MockspressoBuilder
import com.episode6.mockspresso2.plugins.core.reflect.reflectionRealObjectMaker
import com.episode6.mockspresso2.reflect.allConstructors
import com.episode6.mockspresso2.reflect.asKClass
import com.episode6.mockspresso2.reflect.parameterCount
import com.episode6.mockspresso2.reflect.primaryConstructor

/**
 * Instruct this mockspresso instance to create real objects by calling their primary constructor.
 */
public fun MockspressoBuilder.makeRealObjectsUsingPrimaryConstructor(): MockspressoBuilder = makeRealObjectsWith(
  reflectionRealObjectMaker { token.asKClass().primaryConstructor() }
)

/**
 * Instruct this mockspresso instance to create real objects by calling the constructor with the fewest parameters.
 */
public fun MockspressoBuilder.makeRealObjectsUsingShortestConstructor(): MockspressoBuilder = makeRealObjectsWith(
  reflectionRealObjectMaker { token.asKClass().allConstructors().minByOrNull { f -> f.parameterCount() }!! }
)

/**
 * Instruct this mockspresso instance to create real objects by calling the constructor with the most parameters.
 */
public fun MockspressoBuilder.makeRealObjectsUsingLongestConstructor(): MockspressoBuilder = makeRealObjectsWith(
  reflectionRealObjectMaker { token.asKClass().allConstructors().maxByOrNull { f -> f.parameterCount() }!! }
)
