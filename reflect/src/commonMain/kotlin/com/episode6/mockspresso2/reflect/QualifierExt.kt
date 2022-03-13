package com.episode6.mockspresso2.reflect

/**
 * Returns the single qualifier in the receiver list if there is one. Throws a [MultipleQualifierError] if there is
 * more than one qualifier.
 */
fun List<Annotation>.findQualifier(errorContext: () -> String): Annotation? {
  val qualifiers = filter { it.isQualifier() }
  if (qualifiers.size > 1) throw MultipleQualifierError(errorContext())
  return qualifiers.firstOrNull()
}

/**
 * Thrown if ore than one qualifier annotation is found on a given class.
 */
class MultipleQualifierError(context: String) : AssertionError("Multiple qualifier annotations found: $context")
