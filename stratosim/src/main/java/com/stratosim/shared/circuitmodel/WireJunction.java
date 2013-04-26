package com.stratosim.shared.circuitmodel;

import java.io.Serializable;

import com.stratosim.shared.devicemodel.PortFactory;
import com.stratosim.shared.drawing.DrawableContext;

public class WireJunction implements DrawableObject, AbsolutePositionedObject, Serializable {
  private static final long serialVersionUID = -4472463362474272335L;

  private static PortFactory portFactory = new PortFactory("0", new Point(0, 0));

  private Port port;
  private Point location;

  @SuppressWarnings("unused")
  // for GWT RPC
  private WireJunction() {}

  public WireJunction(Point location) {
    this.port = portFactory.create(this);
    this.location = location;
  }

  public Port getOnlyPort() {
    return port;
  }

  @Override
  public int getRotation() {
    return 0;
  }

  @Override
  public boolean isMirrored() {
    return false;
  }

  @Override
  public Point getLocation() {
    return location;
  }

  @Override
  public boolean contains(Point p, int margin) {
    return port.contains(p, margin);
  }

  @Override
  public void draw(DrawableContext context, boolean selected, boolean hovered) {
    getOnlyPort().draw(context, selected, hovered);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    return result;
  }

  // Equals only compares location, not wire connections.
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    WireJunction other = (WireJunction) obj;
    if (location == null) {
      if (other.location != null) return false;
    } else if (!location.equals(other.location)) return false;
    return true;
  }

}
