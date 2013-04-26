package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuithelpers.WireExtender;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;

public class SelectedDeviceState extends AbstractSelectedState implements State {
  private PortOwnerId deviceId;
  private Device shadow;

  SelectedDeviceState(Circuit circuit, PortOwnerId deviceId) {
    super(circuit);

    assert (deviceId != null);
    this.deviceId = deviceId;

    Device selected = getCircuit().getDevice(deviceId);
    // TODO(tpondich): Yea... seriously we need a clonery thing.
    shadow =
        selected.getType().create(selected.getLocation(), selected.getRotation(),
            selected.isMirrored(), selected.getParameter("Name").getValue());
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
    DeviceEditor editor = new DeviceEditor();
    SchematicPanelLinkFactory.create().showEditorBottomRight(editor,
        selected.getLocation().translate(selected.getWidth() / 2, selected.getHeight() / 2));
  }

  @Override
  protected boolean doDrawDrawableObjectAsSelected(DrawableObject drawableObject) {
    return getCircuit().getDevice(deviceId) == drawableObject;
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    State nextState = super.goMouseDown(mousePosition);
    // We need to change the previous of panning to a new instance of this state
    // so that the constructor is called and creates a new widget.
    if (nextState instanceof PanningState) {
      return new PanningState(getCircuit(), mousePosition, new IdleState(getCircuit()), new IdleState(getCircuit()));
    } else {
      return nextState;
    }
  }

  @Override
  public State goMouseWheel(Point mousePosition, boolean isNorth) {
    super.goMouseWheel(mousePosition, isNorth);
    return new IdleState(getCircuit());
  }

  @Override
  public State goPressDelete() {
    getCircuit().removeDevice(deviceId);

    return new IdleState(getCircuit());
  }

  @Override
  public State goPressRotate() {
    shadow.rotateRight();
    getCircuit().replaceDevice(deviceId, shadow);
    rerouteWires();

    return new SelectedDeviceState(getCircuit(), deviceId);
  }

  @Override
  public State goEditorAction(EditorAction a) {
    DeviceEditor.DeviceEditorAction action = (DeviceEditor.DeviceEditorAction) a;
    switch (action) {
      case ROTATE_RIGHT:
        shadow.rotateRight();
        break;
      case ROTATE_LEFT:
        shadow.rotateLeft();
        break;
      case FLIP_HORIZONTAL:
        shadow.flipHorizontal();
        break;
      case FLIP_VERTICAL:
        shadow.flipVertical();
        break;
      default:
        break;
    }

    getCircuit().replaceDevice(deviceId, shadow);
    rerouteWires();

    return new SelectedDeviceState(getCircuit(), deviceId);
  }

  private void rerouteWires() {
    Device selected = getCircuit().getDevice(deviceId);
    for (Port port : selected.getPorts()) {
      for (WireId wireId : port.getWireIds()) {
        Wire oldWire = getCircuit().getWire(wireId);
        WireExtender follower = new WireExtender(oldWire);
        follower.replacePoint(deviceId, getCircuit()
            .getPortIdOfDevicePort(deviceId, port.getName()), port.getLocation());
        Wire newWire = follower.build();
        getCircuit().replaceWire(wireId, newWire);
      }
    }
  }
}
