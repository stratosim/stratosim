package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.drawing.DrawableContext;

interface State {

  /**
   * return the currently selected device type, which should be reflected in the device selection
   * panel
   */
  DeviceType selectedDeviceType();

  // Drawing

  void drawDrawableObject(DrawableContext context, DrawableObject drawableObject);

  void drawShadows(DrawableContext context);

  Cursor getCursor();

  // Transition

  State goMouseDown(Point mousePosition);

  State goMouseUp(Point mousePosition);

  State goMouseMove(Point mousePosition);

  State goMouseWheel(Point mousePosition, boolean isNorth);

  State goMouseOut();

  State goPressDelete();

  State goPressMirrored();

  State goPressRotate();

  State goPressParameters();

  State goPressEscape();

  State goBlur();

  State goEditorAction(EditorAction a);

  State goSelectDeviceType(DeviceType deviceType);

  State goSetEditable(boolean isEditable);

  State goZoomIn(int offsetWidth, int offsetHeight);

  State goZoomOut(int offsetWidth, int offsetHeight);

  State goZoomFit(int offsetWidth, int offsetHeight, int padLeft, int padRight, int padTop,
      int padBottom);

  void initEditor();

}
