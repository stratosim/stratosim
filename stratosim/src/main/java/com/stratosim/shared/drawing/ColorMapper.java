package com.stratosim.shared.drawing;

import com.google.common.collect.ImmutableMap;
import com.stratosim.shared.circuitmodel.Color;

/**
 * Maps nice names for colors to their RGB equivalents. Temporarily until we
 * have a color chooser built into our front end.
 */
public final class ColorMapper {

  private static final ImmutableMap<String, Color> colorMap;
  
  private ColorMapper() {
    throw new UnsupportedOperationException("not instantiable");
  }
  
  static {
    ImmutableMap.Builder<String, Color> builder = ImmutableMap.builder();
    builder.put("red", new Color(1, 0, 0));
    builder.put("orange", new Color(1, 0.65, 0));
    builder.put("yellow", new Color(1, 0.84, 0));
    builder.put("green", new Color(0, 0.5, 0));
    builder.put("blue", new Color(0, 0.4, 1));
    builder.put("purple", new Color(0.5, 0, 0.5));
    colorMap = builder.build();
  }
  
  public static final Color get(String prettyColor) {
    return colorMap.get(prettyColor);
  }
  
}
