package com.stratosim.shared.model.circuit.impl.pointsetindex;

import com.stratosim.shared.model.util.CoordPair;

public interface PointSet {

  boolean isEmpty();

  boolean contains(CoordPair pt);

  PointSet intersect(PointSet other);
}
