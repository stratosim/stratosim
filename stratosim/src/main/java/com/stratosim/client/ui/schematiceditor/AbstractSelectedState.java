package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObjectId;
import com.stratosim.shared.circuitmodel.LabelId;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.WireId;

public class AbstractSelectedState extends AbstractSelectableState {

  protected AbstractSelectedState(Circuit circuit) {
    super(circuit);
  }

  public static State create(Circuit circuit, DrawableObjectId drawableObjectId, Point mousePosition) {

    if (drawableObjectId instanceof PortId) {
      PortId portId = (PortId) drawableObjectId;
      return new PlacingWireFromPortState(circuit, portId, mousePosition);

    } else if (drawableObjectId instanceof LabelId) {
      LabelId labelId = (LabelId) drawableObjectId;
      return new ParameterEditingState(circuit, labelId);

    } else if (drawableObjectId instanceof PortOwnerId) {
      PortOwnerId deviceId = (PortOwnerId) drawableObjectId;
      return new SelectedDeviceState(circuit, deviceId);

    } else if (drawableObjectId instanceof WireId) {
      WireId wireId = (WireId) drawableObjectId;
      return new SelectedWireState(circuit, wireId, mousePosition);

    } else {
      assert (false) : "Invalid Selected DrawableObject";
      return new IdleState(circuit);
    }
  }

  @Override
  public State goMouseUp(Point mousePosition) {
    boolean isDrawableObjectUnderMouse = false;

    // A device's bounds are inclusive of ports and labels, so this is a sufficient test.
    if (getCircuit().getDeviceIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN) != null
        || getCircuit().getWireIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN) != null) {
      isDrawableObjectUnderMouse = true;
    }

    if (!isDrawableObjectUnderMouse) {
      return new IdleState(getCircuit());
    } else {
      return super.goMouseUp(mousePosition);
    }
  }
}
