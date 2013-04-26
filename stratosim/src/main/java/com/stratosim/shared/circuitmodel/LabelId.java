package com.stratosim.shared.circuitmodel;

import java.io.Serializable;


public class LabelId implements DrawableObjectId, Serializable {
  private static final long serialVersionUID = 1237989176555459758L;

  private PortOwnerId portOwnerId;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private LabelId() {}

  LabelId(PortOwnerId portOwnerId) {
    super();
    this.portOwnerId = portOwnerId;
  }

  PortOwnerId getPortOwnerId() {
    return portOwnerId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((portOwnerId == null) ? 0 : portOwnerId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LabelId other = (LabelId) obj;
    if (portOwnerId == null) {
      if (other.portOwnerId != null) return false;
    } else if (!portOwnerId.equals(other.portOwnerId)) return false;
    return true;
  }


}
