package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuithelpers.WireBreaker;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.circuitmodel.WireJunction;
import com.stratosim.shared.drawing.DrawableContext;

public class PlacingWireFromWireState extends AbstractPlacingWireState {
  private WireId startWireId;

  private WireJunction shadowStartJunction;

  PlacingWireFromWireState(Circuit circuit, WireJunction shadowStartJunction, WireId startWireId) {
    super(circuit, shadowStartJunction.getLocation());

    this.startWireId = startWireId;

    this.shadowStartJunction = shadowStartJunction;
  }

  private PortId breakWire() {
    Point shadowStartPoint = shadowStartJunction.getLocation();

    Wire startWire = getCircuit().getWire(startWireId);
    int startSegment =
        startWire.getWireSegmentIndexAt(shadowStartPoint, SchematicPanelLink.CLICK_MARGIN);
    assert (startWire != null);
    assert (startSegment != -1);

    WireBreaker startBreaker = new WireBreaker(startWireId, startSegment, shadowStartPoint);
    startBreaker.commitToCircuit(getCircuit());

    PortId portId = getCircuit().getPortIdOfWireJunctionId(startBreaker.getWireJunctionId());

    return portId;
  }

  protected WireId mouseUpOnPort(PortId endPortId) {
    Port port = getCircuit().getPort(endPortId);

    // The port shouldn't already be connected to the wire
    // that we started from.
    for (WireId wireId : port.getWireIds()) {
      if (wireId.equals(startWireId)) {
        return null;
      }
    }

    PortId startPortId = breakWire();
    WireId newWireId = placeWire(startPortId, endPortId);

    return newWireId;
  }

  protected WireId mouseUpOnWire(WireId endWireId, Point mousePosition) {
    // The wire shouldn't be the wire we started from.
    if (endWireId.equals(startWireId)) {
      return null;
    }

    Wire wire = getCircuit().getWire(endWireId);
    int endSegment = wire.getWireSegmentIndexAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);

    WireBreaker endBreaker = new WireBreaker(endWireId, endSegment, mousePosition);
    endBreaker.commitToCircuit(getCircuit());

    PortId portId = getCircuit().getPortIdOfWireJunctionId(endBreaker.getWireJunctionId());

    return mouseUpOnPort(portId);
  }


  @Override
  public State goMouseUp(Point mousePosition) {
    // since the junction is a shadow, it won't be caught by the
    // landing-on-start-port check in the parent class. so, check manually.
    if (shadowStartJunction.contains(mousePosition, SchematicPanelLink.CLICK_MARGIN)) {
      return sameState();

    } else {
      return super.goMouseUp(mousePosition);
    }
  }

  @Override
  public void drawShadows(DrawableContext context) {
    super.drawShadows(context);
    drawDrawableObject(context, shadowStartJunction);
  }

  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return super.doDrawDrawableObjectAsHovered(drawableObject)
        || shadowStartJunction == drawableObject;
  }
}
