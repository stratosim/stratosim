package com.stratosim.shared.validator;

import java.util.ArrayList;

import com.google.common.collect.Lists;

public class DisjunctionValidator<T> implements Validator<T> {
  String message = "Composite validation failed";
  ArrayList<Validator<T>> validators;

  public DisjunctionValidator() {
    validators = Lists.newArrayList();
  }

  public void addValidator(Validator<T> validator) {
    validators.add(validator);
  }

  public boolean isValid(T circuitObject) {
    boolean valid = false;
    message = "";
    for (Validator<T> validator : validators) {
      // Yes, I mean and the messages are all negative sentences.
      message += validator.getMessage() + " and ";
      if (validator.isValid(circuitObject)) {
        valid = true;
      }
    }
    if (message.length() > 4) {
      message = message.substring(0, message.length() - 4);
    }
    return valid;
  }

  public String getMessage() {
    return message;
  }
}
