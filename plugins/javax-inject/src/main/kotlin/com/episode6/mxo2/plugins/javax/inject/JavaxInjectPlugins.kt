package com.episode6.mxo2.plugins.javax.inject

import com.episode6.mxo2.MockspressoBuilder

fun MockspressoBuilder.makeRealObjectsUsingJavaxInjectRules(): MockspressoBuilder =
  makeRealObjectsWith(javaxRealObjectMaker())

