package com.stratosim.shared.circuitmodel;

import java.io.Serializable;

import com.stratosim.shared.devicemodel.ParameterType;

public class Parameter implements Serializable {
  private static final long serialVersionUID = 2537586956011114534L;

  private ParameterType parameterType;
  private String value;

  @SuppressWarnings("unused")
  // for GWT RPC
  private Parameter() {}

  public Parameter(ParameterType parameterType, String value) {
    this.parameterType = parameterType;
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String v) {
    value = v;
  }

  public ParameterType getType() {
    return parameterType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((parameterType == null) ? 0 : parameterType.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Parameter other = (Parameter) obj;
    if (parameterType == null) {
      if (other.parameterType != null) return false;
    } else if (!parameterType.equals(other.parameterType)) return false;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Parameter [parameterType=" + parameterType + ", value=" + value + "]";
  }
}
