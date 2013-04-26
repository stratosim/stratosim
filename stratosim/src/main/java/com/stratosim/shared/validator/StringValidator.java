package com.stratosim.shared.validator;

import java.io.Serializable;

public interface StringValidator extends Validator<String>, Serializable {
  boolean isValid(String parameter);
}
