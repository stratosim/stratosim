package com.stratosim.shared.validator;

import com.stratosim.shared.circuitmodel.Circuit;

public class PreSimulateValidator implements CircuitValidator {
  private Validator<Circuit> validator;

  public PreSimulateValidator() {
    ConjunctionValidator<Circuit> validator = new ConjunctionValidator<Circuit>();
    validator.addValidator(new CountValidator("Ground", 1, Integer.MAX_VALUE));
    validator.addValidator(new HasAllValidParametersValidator());
    validator.addValidator(new HasAllPortsConnectedValidator());
    DisjunctionValidator<Circuit> hasProbeValidator = new DisjunctionValidator<Circuit>();
    hasProbeValidator.addValidator(new CountValidator("Voltage Probe", 1, Integer.MAX_VALUE));
    hasProbeValidator.addValidator(new CountValidator("Current Probe", 1, Integer.MAX_VALUE));
    validator.addValidator(hasProbeValidator);
    validator.addValidator(new SimulationSettingsValidValidator());

    this.validator = validator;
  }

  @Override
  public String getMessage() {
    return validator.getMessage();
  }

  @Override
  public boolean isValid(Circuit circuit) {
    return validator.isValid(circuit);
  }

}
