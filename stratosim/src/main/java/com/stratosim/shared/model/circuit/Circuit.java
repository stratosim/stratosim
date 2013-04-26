package com.stratosim.shared.model.circuit;

import com.stratosim.shared.model.drawing.Drawable;

public interface Circuit extends Drawable {

  Device newDevice();

  void deleteDevice(Device device);

  Wire newWireBetweenPorts(Port one, Port two);

  void deleteWire(Wire wire);

  /**
   * Get a locator with the chosen margin.
   * 
   * @param margin Locator will return objects within {@code margin} units of the query point.
   * @return The locator.
   */
  Locator getLocator(int margin);
}
