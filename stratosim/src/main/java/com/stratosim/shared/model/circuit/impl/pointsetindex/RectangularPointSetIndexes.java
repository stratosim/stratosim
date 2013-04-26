package com.stratosim.shared.model.circuit.impl.pointsetindex;

import com.stratosim.shared.model.circuit.impl.pointsetindex.impl.BruteForceRectangularPointSetIndex;

public class RectangularPointSetIndexes {

  private RectangularPointSetIndexes() {}

  public static <T> RectangularPointSetIndex<T> newBruteForceInstance() {
    return new BruteForceRectangularPointSetIndex<T>();
  }

}
