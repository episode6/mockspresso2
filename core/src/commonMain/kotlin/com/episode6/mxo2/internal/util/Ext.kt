package com.episode6.mxo2.internal.util

internal fun <T> mlazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

internal fun runOnce(lambda: () -> Unit): Lazy<() -> Unit> = mlazy {
  lambda()
  return@mlazy {}
}
