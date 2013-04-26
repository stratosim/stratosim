package com.stratosim.client.ui.schematiceditor;

import java.util.logging.Logger;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObjectId;
import com.stratosim.shared.circuitmodel.Point;

/**
 * A state where the user has moused down on a circuit object but we don't know if they're selecting
 * or dragging yet.
 */
public class MousingCircuitObjectState extends AbstractPannableState implements State {

  private static Logger logger = Logger.getLogger(MousingCircuitObjectState.class.getName());

  private DrawableObjectId mousedId;
  private Point initialMousePosition;

  MousingCircuitObjectState(Circuit circuit, DrawableObjectId mousedId, Point initialMousePosition) {
    super(circuit);
    this.mousedId = mousedId;
    this.initialMousePosition = initialMousePosition;
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    logger.warning("mouse down on different schematic location");
    return new IdleState(getCircuit());
  }

  @Override
  public State goMouseUp(Point mousePosition) {
    return AbstractSelectedState.create(getCircuit(), mousedId, mousePosition);
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    // The snapping acts as a threshold for drag.
    return AbstractDraggingState.create(getCircuit(), mousedId, initialMousePosition);
  }

}
