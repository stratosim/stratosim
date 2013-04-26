package com.stratosim.shared.validator;

import com.stratosim.shared.circuitmodel.Circuit;

public class NotBlankValidator implements CircuitValidator {

  public NotBlankValidator() {}

  @Override
  public boolean isValid(Circuit circuit) {
    return !circuit.getDevices().isEmpty();
  }

  public String getMessage() {
    return "Must not be blank.";
  }
}
