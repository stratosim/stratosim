package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Point;

interface SchematicPanelLink {
  // TODO(tpondich): This might as well be passed in the constructor
  public static final int CLICK_MARGIN = 10;

  void showEditorCenter(Editor editor, Point position);

  void showEditorBottomRight(Editor editor, Point position);
}
