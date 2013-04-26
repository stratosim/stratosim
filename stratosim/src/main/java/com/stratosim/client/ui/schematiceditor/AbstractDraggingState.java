package com.stratosim.client.ui.schematiceditor;

import java.util.logging.Logger;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObjectId;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.PortId;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.WireId;

public abstract class AbstractDraggingState extends AbstractPannableState implements State {

  private static Logger logger = Logger.getLogger(AbstractDraggingState.class.getName());

  AbstractDraggingState(Circuit circuit) {
    super(circuit);
  }

  /**
   * @param initialMousePosition the mouse position at the start of the drag (the mouse down), even
   *        if the mouse has since moved.
   */
  public static State create(Circuit circuit, DrawableObjectId draggingId,
      Point initialMousePosition) {
    
    if (draggingId instanceof PortId) {
      PortId portId = (PortId) draggingId;
      return new PlacingWireFromPortState(circuit, portId, initialMousePosition);
    } else if (draggingId instanceof PortOwnerId) {
      PortOwnerId deviceId = (PortOwnerId) draggingId;
      // TODO(tpondich): Take initial mouse position to offset the drag.
      return new DraggingDeviceState(circuit, deviceId);

    } else if (draggingId instanceof WireId) {
      WireId wireId = (WireId) draggingId;
      return new DraggingWireState(circuit, wireId, initialMousePosition);

    } else {
      assert (false);
      throw new IllegalStateException();
    }
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    logger.warning("mouse down while dragging");
    return new IdleState(getCircuit());
  }

}
