package com.stratosim.shared.model.circuit.impl.pointsetindex.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSet;
import com.stratosim.shared.model.circuit.impl.pointsetindex.RectangularPointSetIndex;

public class BruteForceRectangularPointSetIndex<T> implements RectangularPointSetIndex<T> {

  private final Multimap<RectangularPointSet, T> objects;

  public BruteForceRectangularPointSetIndex() {
    objects = HashMultimap.create();
  }

  @Override
  public ImmutableCollection<T> get(RectangularPointSet set) {
    checkNotNull(set);

    ImmutableList.Builder<T> hits = ImmutableList.builder();

    for (Entry<RectangularPointSet, T> entry : objects.entries()) {
      if (!set.intersect(entry.getKey()).isEmpty()) {
        hits.add(entry.getValue());
      }
    }

    return hits.build();
  }

  @Override
  public void put(RectangularPointSet set, T t) {
    checkNotNull(set);
    checkNotNull(t);
    objects.put(set, t);
  }

}
