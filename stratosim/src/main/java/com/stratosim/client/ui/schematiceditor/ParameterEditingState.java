package com.stratosim.client.ui.schematiceditor;

import com.stratosim.client.ui.schematiceditor.ParameterEditor.ParameterEditorAction;
import com.stratosim.client.ui.schematiceditor.ParameterEditor.ParameterEditorCloseAction;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.LabelId;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.WireId;

public class ParameterEditingState extends AbstractSelectableState implements State {
  private PortOwnerId deviceId;
  private Device shadow;

  ParameterEditingState(Circuit circuit, LabelId labelId) {
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
    for (Port port : selected.getPorts()) {
      for (WireId wireId : port.getWireIds()) {
        shadow.getPort(port.getName()).addWire(wireId);
      }
    }
  }

  @Override
  public void initEditor() {
    Device selected = getCircuit().getDevice(deviceId);
    ParameterEditor editor = new ParameterEditor(selected, true);
    SchematicPanelLinkFactory.create().showEditorCenter(editor,
        selected.getLabel().getCenterLocation());
  }

  private void replaceDevice() {
    getCircuit().replaceDevice(deviceId, shadow);
  }
  
  @Override
  public State goEditorAction(EditorAction a) {
    if (a instanceof ParameterEditorAction) {
      ParameterEditorAction action = (ParameterEditorAction) a;
  
      shadow.setParameter(action.getParameterName(), action.getValue());
      // TODO(tpondich): Proper morphing of custom types.
  
      return sameState();
      
    } else if (a instanceof ParameterEditorCloseAction) {
      replaceDevice();
      return new IdleState(getCircuit());
      
    } else {
      assert (false): "No other actions are allowed in this state.";
      return null;
    }
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    replaceDevice();

    State nextState = super.goMouseDown(mousePosition);
    // Disallow panning in this state because it returns previous state
    // which will put us in a ParameterEditingState with no editor
    // showing.
    if (nextState instanceof PanningState) {
      return new PanningState(getCircuit(), mousePosition, new IdleState(getCircuit()), new IdleState(getCircuit()));
    } else {
      return nextState;
    }
  }

  @Override
  public State goMouseWheel(Point mousePosition, boolean isNorth) {
    replaceDevice();
    
    super.goMouseWheel(mousePosition, isNorth);
    return new IdleState(getCircuit());
  }

  @Override
  public State goSetEditable(boolean isEditable) {
    replaceDevice();
    
    return super.goSetEditable(isEditable);
  }
}
