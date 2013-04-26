package com.stratosim.shared;

/**
 * Thrown when something disappears from the datastore.
 */
public class PersistenceException extends StratoSimException {

  private static final long serialVersionUID = 1741978625084274654L;

  public PersistenceException() {
    super("persistence exception");
  }
  
}
