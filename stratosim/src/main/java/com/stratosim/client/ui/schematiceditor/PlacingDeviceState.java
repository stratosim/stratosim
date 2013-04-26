package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuithelpers.WireBreaker;
import com.stratosim.shared.circuithelpers.WireBuilder;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.drawing.DrawableContext;

public class PlacingDeviceState extends AbstractSelectableState implements State {

  private Device shadow;

  PlacingDeviceState(Circuit circuit, DeviceType deviceType, int rotation, boolean mirrored) {
    super(circuit);
    this.shadow =
        deviceType.create(
            null,
            rotation,
            mirrored,
            deviceType.getDevicePrefix()
                + Integer.toString(circuit.getDeviceNumber(deviceType.getDevicePrefix())));
  }

  @Override
  public Cursor getCursor() {
    return shadow.getLocation() == null ? super.getCursor() : Cursor.CROSSHAIR;
  }

  @Override
  public DeviceType selectedDeviceType() {
    return shadow.getType();
  }

  @Override
  public State goMouseUp(Point mousePosition) {
    shadow.setLocation(mousePosition);
    shadow.setParameter("Name", shadow.getType().getDevicePrefix()
        + getCircuit().getAndIncrementDeviceNumber(shadow.getType().getDevicePrefix()));
    PortOwnerId newDeviceId = getCircuit().addDevice(shadow);

    // See if the shadow's ports are on a wire and needs to be connected.
    // TODO(tpondich): Shameless copy in DraggingDeviceState.
    for (Port port : shadow.getPorts()) {
      WireId wireId = getCircuit().getWireIdAt(port.getLocation(), SchematicPanelLink.CLICK_MARGIN);

      if (wireId != null) {
        int wireSegment =
            getCircuit().getWire(wireId).getWireSegmentIndexAt(port.getLocation(),
                SchematicPanelLink.CLICK_MARGIN);

        WireBreaker breaker = new WireBreaker(wireId, wireSegment, port.getLocation());
        breaker.commitToCircuit(getCircuit());

        WireBuilder builder = new WireBuilder();
        builder.addPoint(port.getLocation());
        PortId startPortId = getCircuit().getPortIdOfDevicePort(newDeviceId, port.getName());
        PortId endPortId = getCircuit().getPortIdOfWireJunctionId(breaker.getWireJunctionId());
        Wire newWire = builder.build(startPortId, endPortId);
        getCircuit().addWire(newWire);
      }
    }

    // TODO(josh): should this select the newly placed device, or get
    // ready to place another?
    return new PlacingDeviceState(getCircuit(), shadow.getType(), shadow.getRotation(),
        shadow.isMirrored());
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    if (getCircuit().getDeviceIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN) != null ||
        getCircuit().getWireIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN) != null) {
      shadow.setLocation(null);
    } else {
      shadow.setLocation(mousePosition);
    }

    return super.goMouseMove(mousePosition);
  }

  @Override
  public State goPressRotate() {
    shadow.rotateRight();
    return sameState();
  }

  @Override
  public State goPressMirrored() {
    shadow.setMirrored(!shadow.isMirrored());
    return sameState();
  }

  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject obj) {
    return super.doDrawDrawableObjectAsHovered(obj) || obj == shadow || obj == shadow.getLabel();
  }

  @Override
  public void drawShadows(DrawableContext context) {
    // use the superclass to draw the shadow objects
    super.drawDrawableObject(context, shadow);
    super.drawDrawableObject(context, shadow.getLabel());
    for (Port port : shadow.getPorts()) {
      super.drawDrawableObject(context, port);
    }
  }

  @Override
  public State goBlur() {
    return new IdleState(getCircuit());
  }
}
