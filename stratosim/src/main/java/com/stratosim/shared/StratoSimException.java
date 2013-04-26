package com.stratosim.shared;

/**
 * Default exception type for StratoSim.
 */
public class StratoSimException extends Exception {

  private static final long serialVersionUID = -2785532533536154789L;
  
  public StratoSimException(String message) {
    super(message);
  }
  
  public StratoSimException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
