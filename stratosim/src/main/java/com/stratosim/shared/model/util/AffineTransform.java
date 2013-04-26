package com.stratosim.shared.model.util;

public class AffineTransform {
  
  final double[][] d;

  private AffineTransform(double d11, double d12, double d21, double d22) {
    this.d = new double[][] { { d11, d12 }, { d21, d22 } };
  }
  
}
