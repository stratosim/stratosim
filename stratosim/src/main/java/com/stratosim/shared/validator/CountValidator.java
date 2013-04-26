package com.stratosim.shared.validator;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;

public class CountValidator implements CircuitValidator {

  String deviceName;
  int lower;
  int upper;

  public CountValidator(String deviceName, int lower, int upper) {
    this.deviceName = deviceName;
    this.lower = lower;
    this.upper = upper;
  }

  @Override
  public boolean isValid(Circuit circuit) {
    int count = 0;
    for (Device d : circuit.getDevices()) {
      // TODO(tpondich): Encapsulate DeviceType equality ?
      if (d.getType().getName().equals(deviceName)) {
        count++;
      }
    }
    return (count >= lower && count <= upper);
  }

  public String getMessage() {
    if (lower == 1 && upper == Integer.MAX_VALUE) {
      return "Missing " + deviceName;
    } else if (lower == upper) {
      return "Must have exactly " + lower + " " + deviceName + "s";
    }
    return "Must have between " + lower + " and " + upper + " " + deviceName + "s";
  }
}
