package com.stratosim.client.ui.schematiceditor;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.stratosim.shared.circuithelpers.WireBreaker;
import com.stratosim.shared.circuithelpers.WireBuilder;
import com.stratosim.shared.circuithelpers.WireExtender;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.drawing.DrawableContext;

public class DraggingDeviceState extends AbstractDraggingState {
  private PortOwnerId draggingDeviceId;
  private Device dragging;

  private Device shadow;
  // TODO(tpondich): Get rid of this once the transition to ids is complete.
  private Set<Wire> oldWires = Sets.newHashSet();
  private Map<WireId, Wire> realToShadow;

  DraggingDeviceState(Circuit circuit, PortOwnerId draggingDeviceId) {
    super(circuit);
    this.draggingDeviceId = draggingDeviceId;
    dragging = getCircuit().getDevice(draggingDeviceId);
    this.shadow =
        dragging.getType().create(dragging.getLocation(), dragging.getRotation(),
            dragging.isMirrored(), dragging.getParameter("Name").getValue());
    // TODO(tpondich): Should create take params too, instead of doing this
    // copy?
    if (!dragging.getType().hasModel()) {
      shadow.setParameters(dragging.getParameters());
    }

    for (Port port : dragging.getPorts()) {
      for (WireId wireId : port.getWireIds()) {
        shadow.getPort(port.getName()).addWire(wireId);
        Wire wire = getCircuit().getWire(wireId);
        oldWires.add(wire);
      }
    }

    realToShadow = Maps.newHashMap();
  }

  @Override
  public State goMouseUp(Point mousePosition) {
    // Swap in the moved device and replace any changed wires.
    getCircuit().replaceDevice(draggingDeviceId, shadow);
    for (Map.Entry<WireId, Wire> entry : realToShadow.entrySet()) {
      getCircuit().replaceWire(entry.getKey(), entry.getValue());
    }

    // See if the shadow's ports are on a wire and needs to be connected.
    // TODO(tpondich): Shameless copy from PlacingDeviceState. Subtly
    // different.
    for (Port port : shadow.getPorts()) {
      if (port.getWireIds().isEmpty()) {
        WireId wireId =
            getCircuit().getWireIdAt(port.getLocation(), SchematicPanelLink.CLICK_MARGIN);
        if (wireId != null) {
          Wire wire = getCircuit().getWire(wireId);
          int wireSegmentIndex =
              wire.getWireSegmentIndexAt(port.getLocation(), SchematicPanelLink.CLICK_MARGIN);
          WireBreaker breaker = new WireBreaker(wireId, wireSegmentIndex, port.getLocation());
          breaker.commitToCircuit(getCircuit());

          WireBuilder builder = new WireBuilder();
          builder.addPoint(port.getLocation());
          PortId startPortId = getCircuit().getPortIdOfDevicePort(draggingDeviceId, port.getName());
          PortId endPortId = getCircuit().getPortIdOfWireJunctionId(breaker.getWireJunctionId());
          Wire newWire = builder.build(startPortId, endPortId);
          getCircuit().addWire(newWire);
        }
      }
    }

    return new SelectedDeviceState(getCircuit(), draggingDeviceId);
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    // Move shadow device.
    shadow.setLocation(mousePosition);

    // Always base changes on original wire parameters.
    for (Port port : dragging.getPorts()) {
      for (WireId wireId : port.getWireIds()) {
        Wire wire = getCircuit().getWire(wireId);
        realToShadow.put(wireId, wire);
      }
    }

    // Update shadow wires.
    for (Port port : dragging.getPorts()) {
      for (WireId wireId : port.getWireIds()) {
        WireExtender extender = new WireExtender(realToShadow.get(wireId));
        PortId portId = getCircuit().getPortIdOfDevicePort(draggingDeviceId, port.getName());
        extender.replacePoint(draggingDeviceId, portId, shadow.getPort(port.getName())
            .getLocation());

        realToShadow.put(wireId, extender.build());
      }
    }

    return sameState();
  }

  @Override
  public void drawDrawableObject(DrawableContext context, DrawableObject drawableObject) {

    // is this the dragging device
    if (drawableObject == dragging
    // is this a port on the dragging device
        || dragging.getPorts().contains(drawableObject) || drawableObject == dragging.getLabel()
        // is this a wire connected to a port on the dragging device
        || oldWires.contains(drawableObject)) {
      // do nothing

    } else {
      super.drawDrawableObject(context, drawableObject);
    }
  }

  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return drawableObject == shadow;
  }

  @Override
  public void drawShadows(DrawableContext context) {
    super.drawDrawableObject(context, shadow);

    super.drawDrawableObject(context, shadow.getLabel());

    for (Port port : shadow.getPorts()) {
      super.drawDrawableObject(context, port);
    }

    for (Wire shadowWire : realToShadow.values()) {
      super.drawDrawableObject(context, shadowWire);
    }
  }

}
