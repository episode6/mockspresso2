package com.episode6.mxo2.reflect

fun List<Annotation>.findQualifier(errorContext: () -> String): Annotation? {
  val qualifiers = filter { it.isQualifier() }
  if (qualifiers.size > 1) throw MultipleQualifierError(errorContext())
  return qualifiers.firstOrNull()
}

class MultipleQualifierError(context: String) : AssertionError("Multiple qualifier annotations found: $context")
