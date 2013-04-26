package com.stratosim.shared.model.circuit.impl.pointsetindex;

import java.util.Comparator;

import com.stratosim.shared.model.circuit.impl.pointsetindex.impl.RectangularPointSetImpl;

public class RectangularPointSets {

  private RectangularPointSets() {}

  public static RectangularPointSet newRPSet(int minX, int maxX, int minY, int maxY) {
    return new RectangularPointSetImpl(minX, maxX, minY, maxY);
  }

  public static Comparator<RectangularPointSet> compareByMinX() {
    return new Comparator<RectangularPointSet>() {
      @Override
      public int compare(RectangularPointSet o1, RectangularPointSet o2) {
        return compareInts(o1.getMinX(), o2.getMinX());
      }
    };
  }

  public static Comparator<RectangularPointSet> compareByMaxX() {
    return new Comparator<RectangularPointSet>() {
      @Override
      public int compare(RectangularPointSet o1, RectangularPointSet o2) {
        return compareInts(o1.getMaxX(), o2.getMaxX());
      }
    };
  }

  public static Comparator<RectangularPointSet> compareByMinY() {
    return new Comparator<RectangularPointSet>() {
      @Override
      public int compare(RectangularPointSet o1, RectangularPointSet o2) {
        return compareInts(o1.getMaxY(), o2.getMaxY());
      }
    };
  }

  public static Comparator<RectangularPointSet> compareByMaxY() {
    return new Comparator<RectangularPointSet>() {
      @Override
      public int compare(RectangularPointSet o1, RectangularPointSet o2) {
        return compareInts(o1.getMinY(), o2.getMinY());
      }
    };
  }

  private static int compareInts(int a, int b) {
    if (a == b) {
      return 0;
    } else if (a < b) {
      return -1;
    } else {
      return 1;
    }
  }

}
