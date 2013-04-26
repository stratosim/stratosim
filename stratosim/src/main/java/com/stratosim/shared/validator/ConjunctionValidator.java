package com.stratosim.shared.validator;

import java.util.ArrayList;

import com.google.common.collect.Lists;

public class ConjunctionValidator<T> implements Validator<T> {
  String message = "Composite validation failed";
  ArrayList<Validator<T>> validators;

  public ConjunctionValidator() {
    validators = Lists.newArrayList();
  }

  public void addValidator(Validator<T> validator) {
    validators.add(validator);
  }

  public boolean isValid(T circuitObject) {
    boolean valid = true;
    message = "";
    for (Validator<T> validator : validators) {
      if (!validator.isValid(circuitObject)) {
        message += validator.getMessage() + "\n";
        valid = false;
      }
    }
    return valid;
  }

  public String getMessage() {
    return message;
  }
}
