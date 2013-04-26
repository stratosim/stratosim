package com.stratosim.shared.validator;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Port;

public class HasAllPortsConnectedValidator implements CircuitValidator {

  String message = "Not all ports connected";

  public HasAllPortsConnectedValidator() {

  }

  @Override
  public boolean isValid(Circuit circuit) {
    boolean valid = true;
    for (Device device : circuit.getDevices()) {
      for (Port port : device.getPorts()) {
        if (port.getWireIds().isEmpty()) {
          valid = false;
          break;
        }
      }
    }
    return valid;
  }

  public String getMessage() {
    return message;
  }
}
