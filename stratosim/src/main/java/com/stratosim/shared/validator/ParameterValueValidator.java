package com.stratosim.shared.validator;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.shared.circuitmodel.Parameter;

public class ParameterValueValidator implements ParameterValidator {
  private static final long serialVersionUID = 4304355241721299245L;

  private/*final*/StringValidator valueValidator;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private ParameterValueValidator() {}

  public ParameterValueValidator(StringValidator valueValidator) {
    this.valueValidator = checkNotNull(valueValidator);
  }

  @Override
  public boolean isValid(Parameter parameter) {
    return valueValidator.isValid(parameter.getValue());
  }

  public String getMessage() {
    return valueValidator.getMessage();
  }
}
