package com.stratosim.shared.circuitmodel;

import java.io.Serializable;

public class WireId implements DrawableObjectId, Serializable {
  private static final long serialVersionUID = -6000509415632734064L;

  private PortId startPortId;
  private PortId endPortId;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private WireId() {}

  WireId(PortId startPortId, PortId endPortId) {
    this.startPortId = startPortId;
    this.endPortId = endPortId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endPortId == null) ? 0 : endPortId.hashCode());
    result = prime * result + ((startPortId == null) ? 0 : startPortId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    WireId other = (WireId) obj;
    if (endPortId == null) {
      if (other.endPortId != null) return false;
    } else if (!endPortId.equals(other.endPortId)) return false;
    if (startPortId == null) {
      if (other.startPortId != null) return false;
    } else if (!startPortId.equals(other.startPortId)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "WireId [startPortId=" + startPortId + ", endPortId=" + endPortId + "]";
  }
}
