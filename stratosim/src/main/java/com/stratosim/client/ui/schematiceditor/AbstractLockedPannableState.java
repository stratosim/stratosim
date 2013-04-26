package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Point;

public abstract class AbstractLockedPannableState extends AbstractPannableState {
  AbstractLockedPannableState(Circuit circuit) {
    super(circuit);
  }
  
  @Override
  public State goMouseDown(Point mousePosition) {
    // handle panning
    return new PanningState(getCircuit(), mousePosition, this, new LockedState(getCircuit()));
  }
}
