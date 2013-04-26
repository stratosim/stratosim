package com.stratosim.shared.model.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;

import com.stratosim.shared.model.util.proto.Point;
import com.stratosim.shared.model.util.impl.CoordPairImpl;

public class CircuitCoords {

  private CircuitCoords() {}

  public static CircuitCoord newCircuitCoord(int x, int y) {
    return new CoordPairImpl(x, y);
  }

  public static CircuitCoord newCircuitCoord(Point pt) {
    return fromProto(pt);
  }

  public static CircuitCoordOffset newCircuitCoordOffset(int x, int y) {
    return new CoordPairImpl(x, y);
  }

  public static CircuitCoord newCircuitCoordOffset(Point pt) {
    return fromProto(pt);
  }

  public static WindowCoord newWindowCoord(int x, int y) {
    return new CoordPairImpl(x, y);
  }

  public static CircuitCoord newWindowCoord(Point pt) {
    return fromProto(pt);
  }
  
  public static Point asProto(CircuitCoord cc) {
    return Point.newBuilder()
        .setX(cc.getX())
        .setY(cc.getY())
        .build();
  }

  public static Comparator<CoordPair> compareByX() {
    throw new UnsupportedOperationException("unimplemented");
  }

  public static Comparator<CoordPair> compareByY() {
    throw new UnsupportedOperationException("unimplemented");
  }

  private static CoordPairImpl fromProto(Point pt) {
    checkArgument(pt.hasX());
    checkArgument(pt.hasY());

    return new CoordPairImpl(pt.getX(), pt.getY());
  }
}
