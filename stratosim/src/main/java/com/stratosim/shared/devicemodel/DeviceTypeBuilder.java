package com.stratosim.shared.devicemodel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.validator.ParameterValidator;
import com.stratosim.shared.validator.ParameterValueValidator;
import com.stratosim.shared.validator.RegExValidator;

public class DeviceTypeBuilder {
  private String type;
  private String model = null;
  private String prefix = "";
  // TODO(tpondich): Why the hell is this an array?
  private String draw;
  private String spiceTemplate = "";
  private String labelTemplate = "";
  private int w;
  private int h;
  private ImmutableList.Builder<PortFactory> portFactories;
  private Map<String, ParameterTypeBuilder> parameterTypeBuilders;
  private ImmutableList.Builder<ParameterType> parameterTypes;

  public DeviceTypeBuilder() {
    portFactories = ImmutableList.builder();
    parameterTypeBuilders = Maps.newHashMap();

    resetOnBuild();
  }

  private void resetOnBuild() {
    model = null;
    parameterTypes = ImmutableList.builder();
    parameterTypes.add(new ParameterType("Name", new ParameterValueValidator(new RegExValidator(
      "^([a-zA-Z0-9_-]|\\s)+$")), "", type, model));
  }

  public DeviceTypeBuilder width(int w) {
    checkArgument(w > 0);
    this.w = w;
    return this;
  }

  public DeviceTypeBuilder height(int h) {
    checkArgument(h > 0);
    this.h = h;
    return this;
  }

  public DeviceTypeBuilder type(String type) {
    checkNotNull(type);

    this.type = type;
    for (ParameterTypeBuilder parameterTypeBuilder : parameterTypeBuilders.values()) {
      parameterTypeBuilder.deviceType(type);
    }
    return this;
  }

  /**
   * If the model is not specified, the device is custom and does not correspond to a defined model.
   * 
   * @param model
   * @return
   */
  public DeviceTypeBuilder model(String model) {
    checkNotNull(model);

    this.model = model;
    for (ParameterTypeBuilder parameterTypeBuilder : parameterTypeBuilders.values()) {
      parameterTypeBuilder.deviceModel(model);
    }
    return this;
  }

  public DeviceTypeBuilder draw(String draw) {
    // TODO(tpondich): Validation

    this.draw = draw;
    return this;
  }

  /**
   * All variables must be enclosed in {}. For direct insertion of ports or parameters (numeric
   * spice), the variable should be marked with %. For escaped values (user strings), the variable
   * should be marked with $ to trigger escaping. The special variable {%SPICENAME} is the current
   * component number and should be appended to ensure uniqueness of names where appropriate. Models
   * must be specified per device and cannot be share across devices. The ".model" line must use
   * {%MODELNAME.[NAME]} as the type of the model and can include user parameters in its definition.
   * The model can then be referred to by {%MODELNAME.[NAME]}.
   */
  public DeviceTypeBuilder spiceTemplate(String spiceTemplate) {
    // TODO(tpondich): Improve validation.
    checkNotNull(spiceTemplate);

    this.spiceTemplate += spiceTemplate + "\n";
    return this;
  }

  public DeviceTypeBuilder port(String name, int relX, int relY) {
    checkNotNull(name);

    portFactories.add(new PortFactory(name, new Point(relX, relY)));
    return this;
  }

  public DeviceTypeBuilder parameter(String name, ParameterValidator validator) {
    checkNotNull(name);
    checkNotNull(validator);

    parameterTypeBuilders.put(name, new ParameterTypeBuilder().deviceType(type).name(name)
        .validator(validator));
    return this;
  }

  public DeviceTypeBuilder parameterdefault(String name, String value) {
    checkNotNull(name);
    checkNotNull(value);

    parameterTypes.add(parameterTypeBuilders.get(name).defaultValue(value).build());
    return this;
  }

  /**
   * Similar to spiceTemplate, but doesn't have $ operators or %SPICEMODEL. Has %MODEL for the name
   * of a device model.
   * 
   * @param labelTemplate
   * @return
   */
  public DeviceTypeBuilder labelTemplate(String labelTemplate) {
    checkNotNull(labelTemplate);

    this.labelTemplate += labelTemplate + "\n";
    return this;
  }

  public DeviceTypeBuilder prefix(String prefix) {
    checkNotNull(prefix);

    this.prefix = prefix;
    return this;
  }

  public DeviceType build() {
    if (model == null) {
      for (ParameterTypeBuilder parameterTypeBuilder : parameterTypeBuilders.values()) {
        parameterTypes.add(parameterTypeBuilder.defaultValue("").build());
      }
    }

    DeviceType t =
        new DeviceType(type, prefix, model, draw, labelTemplate, spiceTemplate, w, h,
            portFactories.build(), parameterTypes.build());

    resetOnBuild();

    return t;
  }
}
