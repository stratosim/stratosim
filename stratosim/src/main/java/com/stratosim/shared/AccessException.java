package com.stratosim.shared;

/**
 * Thrown when trying to access something without the right permissions.
 */
public class AccessException extends StratoSimException {
  
  private static final long serialVersionUID = 1549490855254573154L;
  
  public AccessException() {
    super("access exception");
  }
  
}
