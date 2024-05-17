package com.wilk2.employeePortal.util;

public class MathUtils {
  /// i want to generatge a sigmoid function
  public static float sigmoid(float x) {
    return (float) (1 / (1 + Math.exp(-x)));
  }
}
