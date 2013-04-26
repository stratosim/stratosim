package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.drawing.DrawableContext;

public class PanningState extends AbstractPannableState {

  private Point initialMousePosition;
  private boolean hasDragged;
  private Point initialPanPosition;
  private State previousState;
  private State cancelState;

  PanningState(Circuit circuit, Point initialMousePosition, State previousState, State cancelState) {
    super(circuit);

    this.initialMousePosition =
        coordinateConvertHelper.circuitCoordToCanvasCoord(initialMousePosition);
    this.hasDragged = false;
    this.initialPanPosition = getCircuit().getPan();
    this.previousState = previousState;
    this.cancelState = cancelState;
  }

  @Override
  public Cursor getCursor() {
    return Cursor.MOVE;
  }

  private void updateWithNewMousePosition(Point mousePositionOnCircuit) {
    Point mousePositionOnCanvas =
        coordinateConvertHelper.circuitCoordToCanvasCoord(mousePositionOnCircuit);
    int dx = mousePositionOnCanvas.getX() - initialMousePosition.getX();
    int dy = mousePositionOnCanvas.getY() - initialMousePosition.getY();

    getCircuit().setPan(initialPanPosition.translate(dx, dy));
  }

  @Override
  public State goMouseUp(Point mousePosition) {

    Point canvasCoord = coordinateConvertHelper.circuitCoordToCanvasCoord(mousePosition);

    // if user didn't drag, just delegate to previous state
    if (!hasDragged && canvasCoord.equals(initialMousePosition)) {
      return previousState.goMouseUp(mousePosition);

    } else { // if user dragged, commit the pan
      updateWithNewMousePosition(mousePosition);
      return previousState;
    }
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    if (mousePosition == null) {
      return cancelState;
    }

    hasDragged = true;
    updateWithNewMousePosition(mousePosition);
    return sameState();
  }

  @Override
  public void drawDrawableObject(DrawableContext context, DrawableObject drawableObject) {
    // delegate
    previousState.drawDrawableObject(context, drawableObject);
  }

  @Override
  public void drawShadows(DrawableContext context) {
    // delegate
    previousState.drawShadows(context);
  }

}
