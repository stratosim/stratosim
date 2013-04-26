package com.stratosim.shared.filemodel;

/**
 * Immutable.
 */
public final class CircuitHash extends StratoSimKey {

  private static final long serialVersionUID = 568047306641886565L;

  @SuppressWarnings("unused")
  // for GWT RPC
  private CircuitHash() {}

  public CircuitHash(String key) {
    super(key);
  }

}
