package com.stratosim.shared.circuitmodel;

import static com.google.common.base.Preconditions.checkArgument;

public class Color {
  private final double red;
  private final double green;
  private final double blue;

  public Color(double red, double green, double blue) {
    checkArgument(red >= 0.0 && red <= 1.0);
    checkArgument(green >= 0.0 && green <= 1.0);
    checkArgument(blue >= 0.0 && blue <= 1.0);
    
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public double getRed() {
    return red;
  }

  public double getGreen() {
    return green;
  }

  public double getBlue() {
    return blue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = (long)(blue * Long.MAX_VALUE);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = (long)(green * Long.MAX_VALUE);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = (long)(red * Long.MAX_VALUE);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Color other = (Color) obj;
    if (blue != other.blue) return false;
    if (green != other.green) return false;
    if (red != other.red) return false;
    return true;
  }
  
  
}
