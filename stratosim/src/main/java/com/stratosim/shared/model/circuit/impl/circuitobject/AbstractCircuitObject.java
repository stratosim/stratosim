package com.stratosim.shared.model.circuit.impl.circuitobject;


public abstract class AbstractCircuitObject implements CircuitObject {
  
  private final Id id;
  
  public AbstractCircuitObject() {
    id = IdFactory.newId();
  }

  @Override
  public Id getId() {
    return id;
  }

}
