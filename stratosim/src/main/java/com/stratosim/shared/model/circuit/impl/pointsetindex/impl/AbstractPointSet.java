package com.stratosim.shared.model.circuit.impl.pointsetindex.impl;

import com.stratosim.shared.model.circuit.impl.pointsetindex.PointSet;
import com.stratosim.shared.model.util.CoordPair;

public abstract class AbstractPointSet implements PointSet {

  public PointSet intersect(PointSet other) {
    return new IntersectedPointSet(this, other);
  }

  private static class IntersectedPointSet extends AbstractPointSet {

    private final PointSet set1;
    private final PointSet set2;

    public IntersectedPointSet(PointSet set1, PointSet set2) {
      this.set1 = set1;
      this.set2 = set2;
    }

    @Override
    public boolean contains(CoordPair pt) {
      return set1.contains(pt) && set2.contains(pt);
    }

    @Override
    public boolean isEmpty() {
      throw new UnsupportedOperationException("i don't know how");
    }
  }
}
