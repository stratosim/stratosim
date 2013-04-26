package com.stratosim.shared.circuitmodel;

public interface AbsolutePositionedObject {
  int getRotation();

  Point getLocation();

  boolean isMirrored();
}
