package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.drawing.DrawableContext;

/**
 * Base class for states that defines default transitions.
 */
public abstract class AbstractState implements State {
  private Circuit circuit;
  protected final CoordinateConvertHelper coordinateConvertHelper;

  AbstractState(Circuit circuit) {
    assert circuit != null;

    this.circuit = circuit;
    this.coordinateConvertHelper = new CoordinateConvertHelper(circuit);
  }

  /**
   * dummy selection determination; subclasses may override. drawCircuitObject will use this method
   * to choose how to draw objects.
   */
  protected boolean doDrawDrawableObjectAsSelected(DrawableObject drawableObject) {
    return false;
  }

  /**
   * dummy hovering determination; subclasses may override. drawCircuitObject will use this method
   * to choose how to draw objects.
   */
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return false;
  }

  protected Circuit getCircuit() {
    return circuit;
  }

  protected State sameState() {
    return this;
  }

  // Overridden methods from State

  @Override
  public DeviceType selectedDeviceType() {
    return null;
  }

  @Override
  public void drawDrawableObject(DrawableContext context, DrawableObject drawableObject) {
    drawableObject.draw(context, doDrawDrawableObjectAsSelected(drawableObject),
        doDrawDrawableObjectAsHovered(drawableObject));
  }

  @Override
  public void drawShadows(DrawableContext context) {
    // do nothing
  }

  @Override
  public Cursor getCursor() {
    return Cursor.POINTER;
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    return sameState();
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    return sameState();
  }

  @Override
  public State goMouseOut() {
    return goMouseMove(null);
  }

  @Override
  public State goMouseUp(Point mousePosition) {
    return sameState();
  }

  @Override
  public State goMouseWheel(Point mousePosition, boolean isNorth) {
    return sameState();
  }

  @Override
  public State goPressDelete() {
    return sameState();
  }

  @Override
  public State goPressRotate() {
    return sameState();
  }

  @Override
  public State goPressMirrored() {
    return sameState();
  }

  @Override
  public State goPressParameters() {
    return sameState();
  }

  @Override
  public State goBlur() {
    return sameState();
  }

  @Override
  public State goSelectDeviceType(DeviceType deviceType) {
    return sameState();
  }

  @Override
  public State goPressEscape() {
    return new IdleState(circuit);
  }

  @Override
  public State goSetEditable(boolean isEditable) {
    if (isEditable) {
      return sameState();
    } else {
      return new LockedState(circuit);
    }
  }

  @Override
  public void initEditor() {

  }

  @Override
  public String toString() {
    String fullname = getClass().getName();
    return fullname.substring(fullname.lastIndexOf(".") + 1);
  }

  @Override
  public State goEditorAction(EditorAction a) {
    return sameState();
  }

  @Override
  public State goZoomIn(int offsetWidth, int offsetHeight) {
    return sameState();
  }

  @Override
  public State goZoomOut(int offsetWidth, int offsetHeight) {
    return sameState();
  }

  @Override
  public State goZoomFit(int offsetWidth, int offsetHeight, int padLeft, int padRight, int padTop,
      int padBottom) {
    return sameState();
  }
}
