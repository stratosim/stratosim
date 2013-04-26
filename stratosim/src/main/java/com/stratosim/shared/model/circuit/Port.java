package com.stratosim.shared.model.circuit;

import com.google.common.collect.ImmutableSet;
import com.stratosim.shared.model.drawing.Hidable;

public interface Port extends Hidable {

  ImmutableSet<Port> getConnectedPorts();

}
