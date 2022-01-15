package com.episode6.mxo2.plugins.mockito

import assertk.Assert
import assertk.assertions.matchesPredicate
import org.mockito.internal.util.MockUtil

fun <T:Any> Assert<T>.isMock() = matchesPredicate { MockUtil.isMock(it) }
fun <T:Any> Assert<T>.isNotMock() = matchesPredicate { !MockUtil.isMock(it) }
