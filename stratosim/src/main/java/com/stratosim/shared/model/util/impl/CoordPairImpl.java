package com.stratosim.shared.model.util.impl;

import com.stratosim.shared.model.util.CircuitCoord;
import com.stratosim.shared.model.util.CircuitCoordOffset;
import com.stratosim.shared.model.util.CoordPair;
import com.stratosim.shared.model.util.WindowCoord;

public class CoordPairImpl implements CoordPair, CircuitCoord, WindowCoord, CircuitCoordOffset {

  private final int x;
  private final int y;

  public CoordPairImpl(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public CircuitCoord add(CircuitCoordOffset cc) {
    return new CoordPairImpl(x + cc.getX(), y + cc.getY());
  }

  public String toString() {
    return "(" + x + "," + y + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CoordPairImpl other = (CoordPairImpl) obj;
    if (x != other.x) return false;
    if (y != other.y) return false;
    return true;
  }

}
