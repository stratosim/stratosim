package com.stratosim.shared.devicemodel;

import java.io.Serializable;

import com.stratosim.shared.circuitmodel.AbsolutePositionedObject;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;

// Immutable
public class PortFactory implements Serializable {
  private static final long serialVersionUID = 8873471466357425550L;

  private/* final */Point relLoc;
  private/* final */String name;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private PortFactory() {}

  public PortFactory(String name, Point relLoc) {
    this.relLoc = relLoc;
    this.name = name;
  }

  public Port create(AbsolutePositionedObject portOwner) {
    return new Port(name, relLoc, portOwner);
  }
}
