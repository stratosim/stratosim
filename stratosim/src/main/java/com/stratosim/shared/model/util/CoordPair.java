package com.stratosim.shared.model.util;

public interface CoordPair {

  int getX();

  int getY();
  
  CoordPair add(CircuitCoordOffset offset);
  
}
