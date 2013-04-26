package com.stratosim.shared.devicemodel;

import static com.google.common.base.Preconditions.checkState;

import com.stratosim.shared.validator.ParameterValidator;

public class ParameterTypeBuilder {
  private ParameterValidator validator;
  private String deviceTypeName = "";
  private String deviceTypeModel = "";
  private String name = "";
  private String defaultValue = "";

  public ParameterTypeBuilder validator(ParameterValidator validator) {
    this.validator = validator;
    return this;
  }

  public ParameterTypeBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ParameterTypeBuilder deviceType(String deviceType) {
    this.deviceTypeName = deviceType;
    return this;
  }

  public ParameterTypeBuilder deviceModel(String deviceModel) {
    this.deviceTypeModel = deviceModel;
    return this;
  }

  public ParameterTypeBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public ParameterType build() {
    ParameterType type = new ParameterType(name, validator, defaultValue, deviceTypeName, deviceTypeModel);
    // TODO(tpondich): Use Optionals.
    checkState(defaultValue == "" || type.getValidator().isValid(type.create()), "Value " + defaultValue +  " invalid for " + type);
    return type;
  }
}
