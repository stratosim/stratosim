package com.stratosim.shared.model.circuit;

import com.stratosim.shared.model.device.DeviceTypeId;
import com.stratosim.shared.model.drawing.Hidable;
import com.stratosim.shared.model.util.CircuitCoordOffset;

public interface Device extends Shadowable<Device.Shadow>, Copyable<Device>, Hidable {

  /**
   * @see Shadowable
   */
  interface Shadow extends Device {
    void commit();
  }
  
  DeviceTypeId getDeviceTypeId();
  
  void setRotation(int rotation);

  void flipHorizontal();

  void flipVertical();

  void translate(CircuitCoordOffset offset);
}
