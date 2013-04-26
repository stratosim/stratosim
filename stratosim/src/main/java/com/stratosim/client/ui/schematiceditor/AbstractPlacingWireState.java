package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuithelpers.WireBuilder;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.circuitmodel.WireJunction;
import com.stratosim.shared.drawing.DrawableContext;

public abstract class AbstractPlacingWireState extends AbstractPannableState {
  private WireBuilder shadowWireBuilder;

  private Port hoveredPort;

  // used transiently -- when the builder spawns a temporary wire for a draw,
  // it will save it here so when the superclass method which subsequently
  // calls
  // this class to determine hover color, the reference is available
  private DrawableObject recentWire;
  private DrawableObject recentWireJunction;

  protected AbstractPlacingWireState(Circuit circuit, Point startLocation) {
    super(circuit);

    shadowWireBuilder = new WireBuilder();
    shadowWireBuilder.addPoint(startLocation);
    // We will call replacePoint on this.
    shadowWireBuilder.addPoint(startLocation);

    recentWire = shadowWireBuilder.buildWithNullPorts();
  }

  protected WireId placeWire(PortId startPortId, PortId endPortId) {
    Wire wire = shadowWireBuilder.build(startPortId, endPortId);
    WireId wireId = getCircuit().addWire(wire);

    return wireId;
  }

  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return recentWire == drawableObject || recentWireJunction == drawableObject
        || hoveredPort == drawableObject;
  }

  @Override
  public void drawShadows(DrawableContext context) {
    drawDrawableObject(context, recentWire);

    if (recentWireJunction != null) {
      drawDrawableObject(context, recentWireJunction);
    }
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    if (mousePosition != null) {
      shadowWireBuilder.replaceLastPoint(mousePosition);
    }

    hoveredPort = getCircuit().getPortAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);

    Wire hoveredWire = getCircuit().getWireAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);
    if (hoveredWire != null) {
      // Mousing over a wire.
      recentWireJunction = new WireJunction(mousePosition);
    } else {
      recentWireJunction = null;
    }

    recentWire = shadowWireBuilder.buildWithNullPorts();

    return sameState();
  }

  protected abstract WireId mouseUpOnPort(PortId portId);

  protected abstract WireId mouseUpOnWire(WireId wireId, Point mousePosition);

  @Override
  public State goMouseUp(Point mousePosition) {
    PortId portId = getCircuit().getPortIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);
    WireId wireId = getCircuit().getWireIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);

    WireId newWireId = null;
    if (portId != null) {
      newWireId = mouseUpOnPort(portId);
    } else if (wireId != null) {
      newWireId = mouseUpOnWire(wireId, mousePosition);
    }

    // landing on nothing -- keep drawing wire
    if (newWireId == null) {
      shadowWireBuilder.addPoint(mousePosition);
      return sameState();
    } else {
      return new SelectedWireState(getCircuit(), newWireId, null);
    }
  }
}
