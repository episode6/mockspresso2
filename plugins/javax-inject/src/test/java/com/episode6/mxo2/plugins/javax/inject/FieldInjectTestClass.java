package com.episode6.mxo2.plugins.javax.inject;

import javax.inject.Inject;

public class FieldInjectTestClass {
  public static class Dep1 {}

  public static class Dep2 {}

  @Inject FieldInjectTestClass() {}

  @Inject public Dep1 d1;
  @Inject public Dep2 d2;
}
