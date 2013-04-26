package com.stratosim.shared.circuithelpers;

import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.Wire;

/**
 * Used when a device connected to a wire moves. Each WireExtender will update a shadow from the old
 * port location to the new port location. Be sure to pass the shadows into this in case both ports
 * are moving. DraggingDeviceState has a correct implementation.
 */
public class WireExtender {
  private WireBuilder builder;
  private Wire oldWire;

  public WireExtender(Wire oldWire) {
    this.oldWire = oldWire;

    builder = new WireBuilder();
    builder.addAllPoints(oldWire.getPoints());
  }

  /**
   * oldPort is the old port, which must be the start or end of the wire passed in. movingPort is
   * the port shadow.
   */
  public void replacePoint(PortOwnerId deviceId, PortId movingPortId, Point newLocation) {
    if (oldWire.getStartPortId().equals(movingPortId)) {
      builder.replaceFirstPoint(newLocation);
    } else if (oldWire.getEndPortId().equals(movingPortId)) {
      builder.replaceLastPoint(newLocation);
    } else {
      assert (false);
    }
  }

  public Wire build() {
    return builder.build(oldWire.getStartPortId(), oldWire.getEndPortId());
  }

  public Wire buildWithNullPorts() {
    return builder.buildWithNullPorts();
  }
}
