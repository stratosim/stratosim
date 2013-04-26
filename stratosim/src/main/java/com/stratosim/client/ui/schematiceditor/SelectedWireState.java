package com.stratosim.client.ui.schematiceditor;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.DrawableObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.circuitmodel.WireJunction;
import com.stratosim.shared.drawing.DrawableContext;

public class SelectedWireState extends AbstractSelectedState {

  private WireId wireId;

  private WireJunction junction;
  private boolean junctionIsHovered = false;

  /**
   * clickPoint is the point on the wire where the user clicked and selected. if this is non-null,
   * the state will show a shadow junction there where the user can start placing a wire. if this is
   * null, no junction will be shown and the user will just be able to manipulate this wire (delete,
   * for example).
   */
  SelectedWireState(Circuit circuit, WireId wireId, Point clickPoint) {
    super(circuit);
    this.wireId = wireId;

    if (clickPoint != null) {
      this.junction = new WireJunction(clickPoint);

    } else {
      this.junction = null;
    }
  }

  @Override
  protected boolean doDrawDrawableObjectAsSelected(DrawableObject drawableObject) {
    return getCircuit().getWire(wireId) == drawableObject;
  }

  @Override
  protected boolean doDrawDrawableObjectAsHovered(DrawableObject drawableObject) {
    return super.doDrawDrawableObjectAsHovered(drawableObject)
        || (junction == drawableObject && junctionIsHovered);
  }

  @Override
  public Cursor getCursor() {
    if (junctionIsHovered) {
      return Cursor.CROSSHAIR;
    } else {
      return super.getCursor();
    }
  }

  @Override
  public State goMouseMove(Point mousePosition) {
    if (junction != null) {
      junctionIsHovered = junction.contains(mousePosition, SchematicPanelLink.CLICK_MARGIN);
    } else {
      junctionIsHovered = false;
    }

    return super.goMouseMove(mousePosition);
  }

  @Override
  public State goMouseDown(Point mousePosition) {
    if (junction != null && junction.contains(mousePosition, SchematicPanelLink.CLICK_MARGIN)) {
      return new PlacingWireFromWireState(getCircuit(), junction, wireId);

    } else {
      return super.goMouseDown(mousePosition);
    }
  }

  @Override
  public State goPressDelete() {
    getCircuit().removeWire(wireId);
    return new IdleState(getCircuit());
  }

  @Override
  public void drawShadows(DrawableContext context) {
    if (junction != null) {
      super.drawDrawableObject(context, junction);
    }
  }

}
