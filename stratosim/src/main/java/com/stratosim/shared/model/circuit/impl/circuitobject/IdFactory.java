package com.stratosim.shared.model.circuit.impl.circuitobject;

import java.util.Random;

import com.stratosim.shared.model.circuit.impl.circuitobject.impl.IdImpl;

public class IdFactory {
  
  private static final int NUM_BYTES = 16;

  private static final Random random = new Random();

  private IdFactory() {}

  public static CircuitObject.Id newId() {
    byte[] data = new byte[NUM_BYTES];
    random.nextBytes(data);
    return new IdImpl(data);
  }

}
