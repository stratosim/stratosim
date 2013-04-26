package com.stratosim.shared.circuitmodel;

import java.io.Serializable;

// Immutable
public abstract class AbstractRelativePositionedObject implements Serializable {
  private static final long serialVersionUID = 7842857891427795259L;

  private AbsolutePositionedObject absolutePositionedObject;
  private Point relLoc;

  // For GWT RPC
  protected AbstractRelativePositionedObject() {}

  protected AbstractRelativePositionedObject(AbsolutePositionedObject device, Point relLoc) {
    this.absolutePositionedObject = device;
    this.relLoc = relLoc;
  }

  public Point getLocation() {
    if (absolutePositionedObject.getLocation() == null) {
      return null;
    }

    int rotatedX = relLoc.getX();
    int rotatedY = relLoc.getY();
    if (absolutePositionedObject.getRotation() == 0) {
      if (!absolutePositionedObject.isMirrored()) {
        rotatedX = relLoc.getX();
        rotatedY = relLoc.getY();
      } else {
        rotatedX = -relLoc.getX();
        rotatedY = relLoc.getY();
      }
    } else if (absolutePositionedObject.getRotation() == 1) {
      if (!absolutePositionedObject.isMirrored()) {
        rotatedX = -relLoc.getY();
        rotatedY = relLoc.getX();
      } else {
        rotatedX = relLoc.getY();
        rotatedY = relLoc.getX();
      }
    } else if (absolutePositionedObject.getRotation() == 2) {
      if (!absolutePositionedObject.isMirrored()) {
        rotatedX = -relLoc.getX();
        rotatedY = -relLoc.getY();
      } else {
        rotatedX = relLoc.getX();
        rotatedY = -relLoc.getY();
      }
    } else if (absolutePositionedObject.getRotation() == 3) {
      if (!absolutePositionedObject.isMirrored()) {
        rotatedX = relLoc.getY();
        rotatedY = -relLoc.getX();
      } else {
        rotatedX = -relLoc.getY();
        rotatedY = -relLoc.getX();
      }
    } else {
      assert (false);
    }

    Point rel = new Point(rotatedX, rotatedY);

    return absolutePositionedObject.getLocation().translate(rel.getX(), rel.getY());
  }

  public Point getRelativeLocation() {
    return relLoc;
  }

  public AbsolutePositionedObject getOwner() {
    return absolutePositionedObject;
  }
}
