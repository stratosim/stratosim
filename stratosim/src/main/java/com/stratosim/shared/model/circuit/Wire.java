package com.stratosim.shared.model.circuit;

import com.google.common.collect.ImmutableList;
import com.stratosim.shared.model.drawing.Hidable;
import com.stratosim.shared.model.util.CircuitCoordOffset;
import com.stratosim.shared.model.util.CoordPair;

public interface Wire extends Shadowable<Wire.Shadow>, Hidable {

  /**
   * @see Shadowable
   */
  interface Shadow extends Wire {
    void commit();
  }

  /**
   * A point in a wire. This may be the start or end point or any corner (where the wire turns).
   * 
   * This is just a view of the underlying wire, so mutations here will show up there, and
   * vice-versa.
   */
  interface Vertex extends CoordPair {
    Wire getWire();

    void translate(CircuitCoordOffset offset);
  }

  ImmutableList<Vertex> getVertices();

  /**
   * A segment of a wire.
   * 
   * This is just a view of the underlying wire, so mutations here will show up there, and
   * vice-versa.
   */
  interface WireSegment {
    ImmutableList<Vertex> getEnds();

    Wire getWire();

    boolean isVertical();

    /**
     * Translates vertical segments in the x-direction and horizontal segments in the y-direction.
     * 
     * @param offset Magnitude and direction of translation. Negative means left/down, positive
     *        means right/up.
     */
    void translate(int offset);
  }

  ImmutableList<WireSegment> getWireSegments();
}
