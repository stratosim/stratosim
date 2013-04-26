package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuithelpers.WireBreaker;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;

public class PlacingWireFromPortState extends AbstractPlacingWireState implements State {

  private PortId startPortId;

  PlacingWireFromPortState(Circuit circuit, PortId startPortId, Point initialMouseLocation) {
    super(circuit, initialMouseLocation);

    this.startPortId = startPortId;
  }

  @Override
  protected WireId mouseUpOnPort(PortId endPortId) {
    Port port = getCircuit().getPort(endPortId);

    // If it's the same port a wire can't be placed.
    if (startPortId.equals(endPortId)) {
      return null;
    }

    // If these two ports are already connected, a wire can't be placed.
    for (WireId wireId : port.getWireIds()) {
      Wire wire = getCircuit().getWire(wireId);
      if (wire.getStartPortId().equals(startPortId) || wire.getEndPortId().equals(startPortId)) {
        return null;
      }
    }

    WireId newWireId = placeWire(startPortId, endPortId);

    return newWireId;
  }

  @Override
  protected WireId mouseUpOnWire(WireId endWireId, Point mousePosition) {
    // The wire shouldn't be a wire the port is already connected to.
    Wire wire = getCircuit().getWire(endWireId);
    if (wire.getStartPortId().equals(startPortId) || wire.getEndPortId().equals(startPortId)) {
      return null;
    }

    int endSegment = wire.getWireSegmentIndexAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);

    WireBreaker endBreaker = new WireBreaker(endWireId, endSegment, mousePosition);
    endBreaker.commitToCircuit(getCircuit());

    PortId portId = getCircuit().getPortIdOfWireJunctionId(endBreaker.getWireJunctionId());

    return mouseUpOnPort(portId);
  }

  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return super.doDrawDrawableObjectAsHovered(drawableObject)
        || getCircuit().getPort(startPortId) == drawableObject;
  }
}
