package com.stratosim.shared.model.util;

public interface CoordOffset {

  int getX();

  int getY();
  
  CoordOffset add(CoordOffset offset);
  
  CoordOffset tranform(AffineTransform transform);

}
