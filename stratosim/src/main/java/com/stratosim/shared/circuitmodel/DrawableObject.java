package com.stratosim.shared.circuitmodel;

import com.stratosim.shared.drawing.DrawableContext;

public interface DrawableObject {
  // TODO(tpondich): Move into UI Binder. (SchematicPanel's attributes ?)

  static final Color DEFAULT_COLOR = new Color(0, 0, 1.0);

  static final Color HOVERED_COLOR = new Color(0.9, 0.9, 1.0);
  static final Color SELECTED_COLOR = new Color(1.0, 0.9, 0.9);

  static final Color UNSELECTED_PORT_COLOR = new Color(0, 0, 0);
  static final Color UNCONNECTED_PORT_COLOR = new Color(1.0, 0.7, 0);

  public static final int PORT_WIDTH = 5;

  static final int TEXT_SIZE = 20;
  static final int TEXT_MARGIN = 5;

  boolean contains(Point p, int margin);

  void draw(DrawableContext context, boolean selected, boolean hovered);
}
