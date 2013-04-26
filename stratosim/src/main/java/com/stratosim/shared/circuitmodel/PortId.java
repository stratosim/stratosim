package com.stratosim.shared.circuitmodel;

import java.io.Serializable;

public class PortId implements DrawableObjectId, Serializable {
  private static final long serialVersionUID = 6139958347470690200L;

  private PortOwnerId portOwnerId;
  private String portName;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private PortId() {}

  PortId(PortOwnerId portOwnerId, String portName) {
    this.portName = portName;
    this.portOwnerId = portOwnerId;
  }

  PortOwnerId getPortOwnerId() {
    return portOwnerId;
  }

  String getPortName() {
    return portName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((portOwnerId == null) ? 0 : portOwnerId.hashCode());
    result = prime * result + ((portName == null) ? 0 : portName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PortId other = (PortId) obj;
    if (portOwnerId == null) {
      if (other.portOwnerId != null) return false;
    } else if (!portOwnerId.equals(other.portOwnerId)) return false;
    if (portName == null) {
      if (other.portName != null) return false;
    } else if (!portName.equals(other.portName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "PortId [portOwnerId=" + portOwnerId + ", portName=" + portName + "]";
  }
}
