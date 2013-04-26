package com.stratosim.client.ui.schematiceditor;

import javax.annotation.Nullable;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Point;

public class CoordinateConvertHelper {
  private final Circuit circuit;
  
  CoordinateConvertHelper(Circuit circuit) {
    this.circuit = circuit;
  }
  
  /**
   * Converts from coordinates relative the circuit drawing origin to coordinates relative to top
   * left of canvas. (At current canvas scale).
   * 
   * @param point
   * @return
   */
   Point circuitCoordToCanvasCoord(@Nullable Point point) {
    if (point == null) {
      return null;
    }

    return point.translate(circuit.getPan().getX(), circuit.getPan().getY());
  }

  /**
   * Converts from coordinates relative the circuit drawing origin to coordinates relative to top
   * left of canvas in pixels.
   * 
   * @param point
   * @return
   */
  Point circuitCoordToPixelCoord(@Nullable Point point) {
    if (point == null) {
      return null;
    }

    return circuitCoordToCanvasCoord(point).scale(circuit.getScale());
  }
  
  /**
   * Convert from canvas pixels to location on circuit by accounting for zoom and pan.
   */
  Point pixelCoordToCircuitCoord(@Nullable Point p) {
    if (p == null) {
      return null;
    }

    double scale = circuit.getScale();
    Point pan = circuit.getPan();

    return new Point((int) (p.getX() / scale) - pan.getX(), ((int) (p.getY() / scale) - pan.getY()));
  }
}
