package com.stratosim.shared.devicemodel;

import java.io.Serializable;

import com.stratosim.shared.circuitmodel.Parameter;
import com.stratosim.shared.validator.ParameterValidator;

// Immutable
public class ParameterType implements Serializable {
  private static final long serialVersionUID = 6796584540731492069L;

  private/* final */String deviceTypeName;
  private/* final */String deviceTypeModel;
  private/* final */String parameterTypeName;
  private/* final */String defaultValue;

  private/* final */ParameterValidator validator;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private ParameterType() {}

  ParameterType(String name, ParameterValidator validator, String defaultValue,
      String deviceTypeName, String deviceTypeModel) {
    this.parameterTypeName = name;
    this.validator = validator;
    this.defaultValue = defaultValue;

    this.deviceTypeName = deviceTypeName;
    this.deviceTypeModel = deviceTypeModel;
  }

  public String getName() {
    return parameterTypeName;
  }

  public ParameterValidator getValidator() {
    return validator;
  }

  public Parameter create() {
    return new Parameter(this, defaultValue);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((deviceTypeModel == null) ? 0 : deviceTypeModel.hashCode());
    result = prime * result + ((deviceTypeName == null) ? 0 : deviceTypeName.hashCode());
    result = prime * result + ((parameterTypeName == null) ? 0 : parameterTypeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ParameterType other = (ParameterType) obj;
    if (deviceTypeModel == null) {
      if (other.deviceTypeModel != null) return false;
    } else if (!deviceTypeModel.equals(other.deviceTypeModel)) return false;
    if (deviceTypeName == null) {
      if (other.deviceTypeName != null) return false;
    } else if (!deviceTypeName.equals(other.deviceTypeName)) return false;
    if (parameterTypeName == null) {
      if (other.parameterTypeName != null) return false;
    } else if (!parameterTypeName.equals(other.parameterTypeName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ParameterType [deviceTypeName=" + deviceTypeName + ", deviceTypeModel="
        + deviceTypeModel + ", parameterTypeName=" + parameterTypeName + ", defaultValue="
        + defaultValue + "]";
  }
}
