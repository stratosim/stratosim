package com.stratosim.shared.validator;

public interface Validator<T> {
  public boolean isValid(T circuitObject);

  public String getMessage();
}
