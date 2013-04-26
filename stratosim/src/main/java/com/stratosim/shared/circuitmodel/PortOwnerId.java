package com.stratosim.shared.circuitmodel;

import java.io.Serializable;

public class PortOwnerId implements DrawableObjectId, Serializable {
  private static final long serialVersionUID = 5212681766116219298L;

  private String deviceId;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private PortOwnerId() {}

  PortOwnerId(String deviceId) {
    this.deviceId = deviceId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PortOwnerId other = (PortOwnerId) obj;
    if (deviceId == null) {
      if (other.deviceId != null) return false;
    } else if (!deviceId.equals(other.deviceId)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "PortOwnerId [deviceId=" + deviceId + "]";
  }
}
