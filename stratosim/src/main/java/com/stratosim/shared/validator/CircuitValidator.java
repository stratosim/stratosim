package com.stratosim.shared.validator;

import com.stratosim.shared.circuitmodel.Circuit;

public interface CircuitValidator extends Validator<Circuit> {
  boolean isValid(Circuit circuit);
}
