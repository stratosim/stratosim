package com.stratosim.shared.model.util;

public interface CircuitCoord extends CoordPair {

  /**
   * Does not mutate. Returns a new object.
   * 
   * @param cc Another pair.
   * @return Vector sum.
   */
  CircuitCoord add(CircuitCoordOffset cc);

}
