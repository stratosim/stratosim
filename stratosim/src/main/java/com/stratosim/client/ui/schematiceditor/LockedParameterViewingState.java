package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.LabelId;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.PortOwnerId;

public class LockedParameterViewingState extends AbstractLockedPannableState implements State {
  private PortOwnerId deviceId;
  private Device shadow;

  LockedParameterViewingState(Circuit circuit, LabelId labelId) {
    super(circuit);
    this.deviceId = circuit.getDeviceIdOfLabelId(labelId);

    // TODO(tpondich): Helpers and Immutablilty
    Device selected = getCircuit().getDevice(deviceId);
    shadow =
        selected
            .getType()
            .getCustom()
            .create(selected.getLocation(), selected.getRotation(), selected.isMirrored(),
                selected.getParameter("Name").getValue());
    shadow.setParameters(selected.getParameters());
  }

  @Override
  public void initEditor() {
    Device selected = getCircuit().getDevice(deviceId);
    ParameterEditor editor = new ParameterEditor(selected, false);
    SchematicPanelLinkFactory.create().showEditorCenter(editor,
        selected.getLabel().getCenterLocation());
  }

  @Override
  public State goEditorAction(EditorAction a) {
    assert(false): "Locked Viewer should not receive EditorActions!";

    return sameState();
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    return new LockedState(getCircuit());
  }

  @Override
  public State goMouseWheel(Point mousePosition, boolean isNorth) {
    super.goMouseWheel(mousePosition, isNorth);
    return new LockedState(getCircuit());
  }
  
  // TODO(tpondich): There should be an abstract locked state with this code.
  // It is shared with LockedParameterViewingState.
  @Override
  public State goSetEditable(boolean isEditable) {
    if (isEditable) {
      return new IdleState(getCircuit());
    } else {
      return sameState();
    }
  }
}
