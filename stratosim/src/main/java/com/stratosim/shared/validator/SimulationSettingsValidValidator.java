package com.stratosim.shared.validator;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.SimulationSettings;

public class SimulationSettingsValidValidator implements CircuitValidator {

  private String message = "Invalid simulation settings";
  
  // TODO(tpondich): Duplicated in panel, partially in device manager instance.
  private/*final*/static String DECIMAL = "[0-9]+(?:\\.[0-9]+)?";
  private/*final*/static String NUMERIC_VALUE_BASE = DECIMAL + "(?:e-?" + DECIMAL + ")?(?:m|u|n|p|f|k|meg|g|t)?";
  private/*final*/static StringValidator frequencyValidator = new RegExValidator("^" + NUMERIC_VALUE_BASE + "Hz" + "$");
  private/*final*/static StringValidator timeValidator = new RegExValidator("^" + NUMERIC_VALUE_BASE + "s" + "$");

  public SimulationSettingsValidValidator() {

  }

  @Override
  public boolean isValid(Circuit circuit) {
    boolean valid = true;
    message = "";
    SimulationSettings settings = circuit.getSimulationSettings();
    if (settings.isTransient()) {
      if (!timeValidator.isValid(settings.getTransientDuration())) {
        message += "Transient duration invalid\n";
        valid = false;
      }
    } else {
      if (!frequencyValidator.isValid(settings.getStartFrequency())) {
        message += "Start frequency invalid\n";
        valid = false;
      }
      if (!frequencyValidator.isValid(settings.getStopFrequency())) {
        message += "Stop frequency invalid\n";
        valid = false;
      }
    }
    
    return valid;
  }

  public String getMessage() {
    return message;
  }
}
