package com.stratosim.shared.circuitmodel;

import java.io.Serializable;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.stratosim.shared.drawing.DrawableContext;

public class Port extends AbstractRelativePositionedObject implements DrawableObject, Serializable {
  private static final long serialVersionUID = -6283383772388924659L;

  private String name;

  private Set<WireId> wireIds;

  @SuppressWarnings("unused")
  // for GWT RPC
  private Port() {}

  public Port(String name, Point relLoc, AbsolutePositionedObject device) {
    super(device, relLoc);

    this.name = name;
    this.wireIds = Sets.newHashSet();
  }

  public boolean contains(Point p, int margin) {
    if (p == null) {
      return false;
    }

    if (p.getX() > getLocation().getX() - margin && p.getX() < getLocation().getX() + margin
        && p.getY() > getLocation().getY() - margin && p.getY() < getLocation().getY() + margin) {
      return true;
    }
    return false;
  }

  public String getName() {
    return name;
  }

  public ImmutableSet<WireId> getWireIds() {
    return ImmutableSet.copyOf(wireIds);
  }

  // TODO(tpondich): Once the shadow device is clonable, we can make these
  // package scope.
  public void addWire(WireId wireId) {
    wireIds.add(wireId);
  }

  public void removeWire(WireId wireId) {
    wireIds.remove(wireId);
  }

  @Override
  public void draw(DrawableContext context, boolean selected, boolean hovered) {
    
    if (getLocation() == null) {
      return;
    }
    
    context.save();

    if (hovered) {
      context.setColor(HOVERED_COLOR.getRed(), HOVERED_COLOR.getGreen(), HOVERED_COLOR.getBlue());
    } else if (wireIds.isEmpty()) {
      context.setColor(UNCONNECTED_PORT_COLOR.getRed(), UNCONNECTED_PORT_COLOR.getGreen(),
          UNCONNECTED_PORT_COLOR.getBlue());
    } else {
      context.setColor(UNSELECTED_PORT_COLOR.getRed(), UNSELECTED_PORT_COLOR.getGreen(),
          UNSELECTED_PORT_COLOR.getBlue());
    }

    context.newPath();
    context.arc(getLocation().getX(), getLocation().getY(), PORT_WIDTH, 0, Math.PI * 2);
    context.fill();

    context.restore();
  }

  @Override
  public String toString() {
    return "Port [name=" + name + ", wireIds=" + wireIds + "]";
  }
}
