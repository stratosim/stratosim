package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.DrawableObjectId;
import com.stratosim.shared.circuitmodel.LabelId;
import com.stratosim.shared.circuitmodel.Point;

public class LockedState extends AbstractLockedPannableState {
  private DrawableObjectId hoveredDrawableObjectId;
  private DrawableObject hoveredDrawableObject;

  LockedState(Circuit circuit) {
    super(circuit);
  }
  
  @Override
  public Cursor getCursor() {
    if (hoveredDrawableObjectId != null) {
      return Cursor.POINTER;
    }
    
    else return super.getCursor();
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    hoveredDrawableObjectId = null;
    hoveredDrawableObject = null;

    LabelId labelId = getCircuit().getLabelIdAt(mousePosition, SchematicPanelLink.CLICK_MARGIN);
    if (labelId != null) {
      hoveredDrawableObjectId = labelId;
      hoveredDrawableObject = getCircuit().getLabel(labelId);
      return sameState();
    }

    return sameState();
  }
  
  @Override
  public State goMouseDown(Point mousePosition) {
    if (hoveredDrawableObjectId != null) {
      return new LockedParameterViewingState(getCircuit(), (LabelId)hoveredDrawableObjectId);
    }
    
    return super.goMouseDown(mousePosition);
  }
  
  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return hoveredDrawableObject == drawableObject;
  }

  @Override
  public State goSetEditable(boolean isEditable) {
    if (isEditable) {
      return new IdleState(getCircuit());
    } else {
      return sameState();
    }
  }
}
