package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuithelpers.WireDeformer;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Wire;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.drawing.DrawableContext;

public class DraggingWireState extends AbstractDraggingState {
  private WireId draggingId;

  private WireDeformer wireDeformer;

  private Wire recentWire;

  DraggingWireState(Circuit circuit, WireId wireId, Point initialMousePosition) {
    super(circuit);

    assert (wireId != null);
    assert (initialMousePosition != null);

    this.draggingId = wireId;
    Wire draggingWire = getCircuit().getWire(wireId);
    int nudgingIndex =
        draggingWire.getWireSegmentIndexAt(initialMousePosition, SchematicPanelLink.CLICK_MARGIN);

    this.wireDeformer = new WireDeformer(draggingWire, nudgingIndex);
  }

  @Override
  public State goMouseUp(Point mousePosition) {
    Wire newWire = wireDeformer.build();
    getCircuit().replaceWire(draggingId, newWire);

    return new SelectedWireState(getCircuit(), draggingId, null);
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    wireDeformer.setPosition(mousePosition);

    return sameState();
  }

  @Override
  public void drawDrawableObject(DrawableContext context, DrawableObject drawableObject) {

    if (drawableObject != getCircuit().getWire(draggingId)) {
      super.drawDrawableObject(context, drawableObject);
    }
  }

  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return drawableObject == recentWire;
  }

  @Override
  public void drawShadows(DrawableContext context) {
    recentWire = wireDeformer.buildWithNullPorts();
    super.drawDrawableObject(context, recentWire);
  }

}
