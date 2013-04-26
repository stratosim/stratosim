package com.stratosim.shared.validator;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Parameter;

public class HasAllValidParametersValidator implements CircuitValidator {

  String message = "Invalid parameter in circuit";

  public HasAllValidParametersValidator() {

  }

  @Override
  public boolean isValid(Circuit circuit) {
    boolean valid = true;
    message = "";
    for (Device device : circuit.getDevices()) {
      for (Parameter parameter : device.getParameters()) {
        if (!parameter.getType().getValidator().isValid(parameter)) {
          message +=
              "Value '" + parameter.getValue() + "' invalid for " + device.getType().getName()
                  + " " + parameter.getType().getName() + "\n";
          valid = false;
        }
      }
    }
    return valid;
  }

  public String getMessage() {
    return message;
  }
}
