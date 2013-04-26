package com.stratosim.shared.model.circuit.impl.pointsetindex;

import com.google.common.collect.ImmutableCollection;

public interface RectangularPointSetIndex<T> {

  ImmutableCollection<T> get(RectangularPointSet set);

  void put(RectangularPointSet set, T t);
}
