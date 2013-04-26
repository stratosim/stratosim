package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.DrawableObjectId;
import com.stratosim.shared.circuitmodel.LabelId;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.WireId;

/**
 * A state where selecting circuit objects can occur.
 */
public abstract class AbstractSelectableState extends AbstractPlaceableState implements State {
  private DrawableObjectId hoveredDrawableObjectId = null;
  private DrawableObject hoveredDrawableObject = null;

  public AbstractSelectableState(Circuit circuit) {
    super(circuit);
  }

  @Override
  public Cursor getCursor() {
    Cursor cursor = Cursor.ARROW;
    if (hoveredDrawableObject == null) {
      cursor = super.getCursor();
    } else if (hoveredDrawableObject instanceof Port) {
      cursor = Cursor.CROSSHAIR;
    } else {
      cursor = Cursor.POINTER;
    }
    return cursor;
  }

  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return hoveredDrawableObject == drawableObject;
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    // TODO(tpondich): Switch to using ids.
    PortOwnerId deviceId =
        getCircuit().getDeviceIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);
    hoveredDrawableObjectId = null;
    hoveredDrawableObject = null;

    if (deviceId != null) {
      PortId portId = getCircuit().getPortIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);
      if (portId != null) {
        hoveredDrawableObjectId = portId;
        hoveredDrawableObject = getCircuit().getPort(portId);
        return sameState();
      }

      LabelId labelId = getCircuit().getLabelIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);
      if (labelId != null) {
        hoveredDrawableObjectId = labelId;
        hoveredDrawableObject = getCircuit().getLabel(labelId);
        return sameState();
      }

      hoveredDrawableObjectId = deviceId;
      hoveredDrawableObject = getCircuit().getDevice(deviceId);
      return sameState();
    }

    WireId wireId = getCircuit().getWireIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);
    if (wireId != null) {
      hoveredDrawableObjectId = wireId;
      hoveredDrawableObject = getCircuit().getWire(wireId);
      return sameState();
    }

    return sameState();
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    if (hoveredDrawableObjectId != null) {
      return new MousingCircuitObjectState(getCircuit(), hoveredDrawableObjectId, mousePosition);
    } else {
      // Let parent take care of panning.
      return super.goMouseDown(mousePosition);
    }
  }

}
