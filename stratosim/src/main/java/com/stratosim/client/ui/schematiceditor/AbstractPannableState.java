package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.devicemodel.DeviceType;

public abstract class AbstractPannableState extends AbstractState {
  private final static double ZOOM_MULTIPLIER = 0.8;
  private final static double ZOOM_BOUND = 4.0;

  AbstractPannableState(Circuit circuit) {
    super(circuit);
  }

  // Overridden methods from State

  @Override
  public Cursor getCursor() {
    return Cursor.CROSSHAIR;
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    // handle panning
    return new PanningState(getCircuit(), mousePosition, this, new IdleState(getCircuit()));
  }

  private void zoom(Point fixedPoint, boolean isZoomIn) {
    double zoom = (!isZoomIn ? ZOOM_MULTIPLIER : 1.0 / ZOOM_MULTIPLIER);
    double oldScale = getCircuit().getScale();
    double newScale = oldScale * zoom;
    if (newScale < 1.0 / ZOOM_BOUND) {
      newScale = 1.0 / ZOOM_BOUND;
    } else if (newScale > ZOOM_BOUND) {
      newScale = ZOOM_BOUND;
    }

    // origin relative to event in old scale.
    int originX = (fixedPoint.getX() + getCircuit().getPan().getX());
    int originY = (fixedPoint.getY() + getCircuit().getPan().getY());
    // delta caused by zoom.
    int deltaX = (int) (originX * oldScale / newScale) - originX;
    int deltaY = (int) (originY * oldScale / newScale) - originY;

    getCircuit().setPan(getCircuit().getPan().translate(deltaX, deltaY));
    getCircuit().setScale(newScale);
  }

  @Override
  public State goMouseWheel(Point mousePosition, boolean isNorth) {
    zoom(mousePosition, isNorth);

    return sameState();
  }

  @Override
  public State goZoomIn(int offsetWidth, int offsetHeight) {
    zoom(new Point(offsetWidth / 2, offsetHeight / 2), true);

    return sameState();
  }

  @Override
  public State goZoomOut(int offsetWidth, int offsetHeight) {
    zoom(new Point(offsetWidth / 2, offsetHeight / 2), false);

    return sameState();
  }

  @Override
  public State goZoomFit(int offsetWidth, int offsetHeight, int padLeft, int padRight, int padTop,
      int padBottom) {

    Point topLeft = getCircuit().getTopLeft();
    Point bottomRight = getCircuit().getBottomRight();

    double width = bottomRight.getX() - topLeft.getX();
    double height = bottomRight.getY() - topLeft.getY();
    
    if (width > 0 && height > 0 && offsetWidth > padLeft + padRight && offsetHeight > padTop + padBottom) {
      double scaleX = (offsetWidth - (padLeft + padRight)) / width;
      double scaleY = (offsetHeight - (padTop + padBottom)) / height;
      double scale = Math.min(scaleX, scaleY);

      getCircuit().setScale(scale);
      getCircuit().setPan(
          topLeft.translate((int) (-padLeft / scale), (int) (-padTop / scale)).scale(-1));
    }

    return sameState();
  }

  @Override
  public State goSelectDeviceType(DeviceType deviceType) {
    assert (false) : "This can't happen.";

    return sameState();
  }
}
