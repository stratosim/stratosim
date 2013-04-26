package com.stratosim.shared.validator;

import com.google.gwt.regexp.shared.RegExp;

public class RegExValidator implements StringValidator {
  private static final long serialVersionUID = 4304355241721299245L;

  // Cannot store just RegExp because of Serializability.
  private String patternString;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private RegExValidator() {}

  public RegExValidator(String regex) {
    this.patternString = regex;
  }

  @Override
  public boolean isValid(String string) {
    // TODO(tpondich): Maybe cache the compiled pattern. Sadly everything is always so fast any
    // optimization is pointless. I miss slow machines that made me optimize.
    return RegExp.compile(patternString).test(string);
  }

  public String getMessage() {
    return "RegEx doesn't match";
  }
}
