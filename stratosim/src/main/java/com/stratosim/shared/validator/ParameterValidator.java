package com.stratosim.shared.validator;

import java.io.Serializable;

import com.stratosim.shared.circuitmodel.Parameter;

public interface ParameterValidator extends Validator<Parameter>, Serializable {
  boolean isValid(Parameter parameter);
}
