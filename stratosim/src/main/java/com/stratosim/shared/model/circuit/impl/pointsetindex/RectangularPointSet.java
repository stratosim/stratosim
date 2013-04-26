package com.stratosim.shared.model.circuit.impl.pointsetindex;

public interface RectangularPointSet extends PointSet {

  // all methods return inclusive bounds

  int getMinX();

  int getMaxX();

  int getMaxY();

  int getMinY();
}
