package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.devicemodel.DeviceType;

public abstract class AbstractPlaceableState extends AbstractPannableState {

  AbstractPlaceableState(Circuit circuit) {
    super(circuit);
  }

  @Override
  public State goSelectDeviceType(DeviceType deviceType) {
    return new PlacingDeviceState(getCircuit(), deviceType, 0, false);
  }
}
