package com.stratosim.shared.model.circuit.impl.circuitobject.impl;

import java.util.Arrays;

import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObject;

public class IdImpl implements CircuitObject.Id {

  private final byte[] data;

  /**
   * Creates a new id.
   * 
   * @param safeData Bytes that will never be changed.
   */
  public IdImpl(byte[] safeData) {
    this.data = safeData;
  }

  @Override
  public byte[] asBytes() {
    return Arrays.copyOf(data, data.length);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(data);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    IdImpl other = (IdImpl) obj;
    if (!Arrays.equals(data, other.data)) {
      return false;
    }
    return true;
  }
}
