package com.stratosim.shared.model.circuit.impl.circuitobject;

public interface CircuitObject {
  /**
   * Implements {@code hashCode()} and {@code equals()}.
   */
  interface Id {
    /**
     * @return A copy of the internal id data. The caller may safely mutate this.
     */
    byte[] asBytes();
  }

  Id getId();
}
