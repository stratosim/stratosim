package com.stratosim.shared.circuitmodel;

import java.io.Serializable;

import com.stratosim.shared.drawing.DrawableContext;

// Immutable
public class WireSegment implements DrawableObject, Serializable {
  private static final long serialVersionUID = -3225697797704974706L;

  private Point p1;
  private Point p2;
  boolean isVertical;

  // for GWT RPC
  @SuppressWarnings("unused")
  private WireSegment() {}

  WireSegment(Point p, boolean isVertical) {
    this.p1 = p;
    this.p2 = p;
    this.isVertical = isVertical;
  }

  public WireSegment(Point p1, Point p2) {
    assert (!p1.equals(p2));

    this.p1 = p1;
    this.p2 = p2;

    inferIsVertical();
  }

  private void inferIsVertical() {
    if (p1.getX() == p2.getX()) {
      isVertical = true;
    } else if (p1.getY() == p2.getY()) {
      isVertical = false;
    } else {
      assert (false);
    }
  }

  public boolean isHorizontal() {
    return !isVertical;
  }

  public boolean isVertical() {
    return isVertical;
  }

  public Point getStartPosition() {
    return p1;
  }

  public Point getEndPosition() {
    return p2;
  }

  public int getLinearPosition() {
    if (isVertical()) {
      return p1.getX();
    } else {
      return p1.getY();
    }
  }

  public int getStartLinearPosition() {
    if (isVertical()) {
      return p1.getY();
    } else {
      return p1.getX();
    }
  }

  public int getEndLinearPosition() {
    if (isVertical()) {
      return p2.getY();
    } else {
      return p2.getX();
    }
  }

  public boolean contains(Point p, int margin) {
    if (p == null) {
      return false;
    }

    Point topLeft;
    Point bottomRight;

    if (isVertical()) {
      // Vertical
      topLeft = new Point(p1.getX() - margin, Math.min(p1.getY(), p2.getY()));
      bottomRight = new Point(p1.getX() + margin, Math.max(p1.getY(), p2.getY()));
    } else {
      // Horizontal
      topLeft = new Point(Math.min(p1.getX(), p2.getX()), p1.getY() - margin);
      bottomRight = new Point(Math.max(p1.getX(), p2.getX()), p1.getY() + margin);
    }

    if (p.getX() >= topLeft.getX() && p.getX() <= bottomRight.getX() && p.getY() >= topLeft.getY()
        && p.getY() <= bottomRight.getY()) {
      return true;
    }

    return false;
  }

  @Override
  public void draw(DrawableContext context, boolean selected, boolean hovered) {
    context.save();
    context.newPath();
    context.moveTo(getStartPosition().getX(), getStartPosition().getY());
    context.lineTo(getEndPosition().getX(), getEndPosition().getY());
    context.stroke();
    context.restore();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (isVertical ? 1231 : 1237);
    result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
    result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    WireSegment other = (WireSegment) obj;
    if (isVertical != other.isVertical) return false;
    if (p1 == null) {
      if (other.p1 != null) return false;
    } else if (!p1.equals(other.p1)) return false;
    if (p2 == null) {
      if (other.p2 != null) return false;
    } else if (!p2.equals(other.p2)) return false;
    return true;
  }
}
