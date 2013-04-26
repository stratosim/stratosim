package com.stratosim.shared.model.circuit.impl.pointsetindex.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.stratosim.shared.model.circuit.impl.pointsetindex.PointSet;
import com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSet;
import com.stratosim.shared.model.util.CoordPair;

public class RectangularPointSetImpl extends AbstractPointSet implements RectangularPointSet {

  private final int minX, maxX, minY, maxY;

  public RectangularPointSetImpl(int minX, int maxX, int minY, int maxY) {
    this.minX = minX;
    this.maxX = maxX;
    this.minY = minY;
    this.maxY = maxY;
  }

  @Override
  public boolean contains(CoordPair pt) {
    checkNotNull(pt);

    return minX <= pt.getX() && pt.getX() <= maxX && minY <= pt.getY() && pt.getY() <= maxY;
  }

  @Override
  public PointSet intersect(PointSet otherSet) {
    checkNotNull(otherSet);

    if (!(otherSet instanceof RectangularPointSetImpl)) {
      return super.intersect(otherSet);
    }

    RectangularPointSetImpl other = (RectangularPointSetImpl) otherSet;
    return new RectangularPointSetImpl(max(minX, other.getMinX()), min(maxX, other.getMaxX()), max(
        minY, other.getMinY()), min(maxY, other.getMaxY()));
  }

  @Override
  public boolean isEmpty() {
    return minX > maxX || minY > maxY;
  }

  @Override
  public int getMinX() {
    return minX;
  }

  @Override
  public int getMaxX() {
    return maxX;
  }

  @Override
  public int getMaxY() {
    return maxY;
  }

  @Override
  public int getMinY() {
    return minY;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + minY;
    result = prime * result + minX;
    result = prime * result + maxX;
    result = prime * result + maxY;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RectangularPointSetImpl other = (RectangularPointSetImpl) obj;
    if (minY != other.minY) return false;
    if (minX != other.minX) return false;
    if (maxX != other.maxX) return false;
    if (maxY != other.maxY) return false;
    return true;
  }

}
