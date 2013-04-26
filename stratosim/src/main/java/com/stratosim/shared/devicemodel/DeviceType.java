package com.stratosim.shared.devicemodel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Parameter;
import com.stratosim.shared.circuitmodel.Point;
import com.stratosim.shared.circuitmodel.Port;

// Immutable
public class DeviceType implements Serializable {
  private static final long serialVersionUID = -2532943118245267162L;

  private/* final */String deviceTypeName;
  private/* final */String devicePrefix;
  // When this is null, it's a custom device.
  private/* final */String deviceModelName;

  private/* final */String spiceTemplate;
  private/* final */String postScriptImage;
  private/* final */String labelTemplate;
  private/* final */int w;
  private/* final */int h;
  private/* final */ImmutableList<PortFactory> portFactories;
  private/* final */ImmutableList<ParameterType> parameterTypes;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private DeviceType() {}

  DeviceType(String type, String prefix, String model, String postScriptImage, String labelTemplate,
      String spiceTemplate, int w, int h, ImmutableList<PortFactory> portFactories,
      ImmutableList<ParameterType> parameterTypes) {
    // TODO(tpondich): asserts
    
    this.deviceTypeName = type;
    this.devicePrefix = prefix;
    this.deviceModelName = model;
    this.postScriptImage = postScriptImage;
    this.labelTemplate = labelTemplate;
    this.spiceTemplate = spiceTemplate;
    this.w = w;
    this.h = h;
    this.portFactories = portFactories;
    this.parameterTypes = parameterTypes;
  }

  public Device create(@Nullable Point location, int rotation, boolean mirrored, String nameString) {
    checkNotNull(nameString);
    checkArgument(rotation >=0 && rotation <= 3);
    
    // TODO(tpondich): Make a create that takes parameters then add this check.
    // Preconditions.checkState(hasModel());
    ImmutableList.Builder<Parameter> parameterBuilder = ImmutableList.builder();

    for (ParameterType pf : parameterTypes) {
      parameterBuilder.add(pf.create());
    }

    Device device = new Device(this, location, rotation, mirrored, parameterBuilder.build());

    ImmutableList.Builder<Port> portBuilder = ImmutableList.builder();
    for (PortFactory pf : portFactories) {
      portBuilder.add(pf.create(device));
    }
    device.setPorts(portBuilder.build());

    device.setParameter("Name", nameString);

    return device;
  }

  public int getW() {
    return w;
  }

  public int getH() {
    return h;
  }

  public String getDevicePrefix() {
    return devicePrefix;
  }

  public String getFullName() {
    String modelString = deviceModelName == null ? "" : deviceModelName;
    return modelString + " " + deviceTypeName;
  }

  public String getName() {
    return deviceTypeName;
  }

  public @Nullable String getModel() {
    return deviceModelName;
  }

  public boolean hasModel() {
    return deviceModelName != null;
  }

  public String getPostScriptImage() {
    return postScriptImage;
  }

  public String getLabelTemplate() {
    return labelTemplate;
  }

  public String getSpiceTemplate() {
    return spiceTemplate;
  }

  public ImmutableList<ParameterType> getParameterTypes() {
    return ImmutableList.copyOf(parameterTypes);
  }

  public DeviceType getCustom() {
    // Passing in the regular parameter types with defaults should be okay
    // since this type of device should always have parameters explicitly set after creation.
    // Clearing it is only to make debugging less confusing.
    ImmutableList.Builder<ParameterType> emptyParameterTypes = ImmutableList.builder();
    for (ParameterType parameterType : parameterTypes) {
      emptyParameterTypes.add(new ParameterType(parameterType.getName(), parameterType
          .getValidator(), "", deviceTypeName, null));
    }

    DeviceType customDeviceType =
        new DeviceType(deviceTypeName, devicePrefix, null, postScriptImage, labelTemplate, spiceTemplate, w, h,
            portFactories, emptyParameterTypes.build());
    return customDeviceType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((deviceModelName == null) ? 0 : deviceModelName.hashCode());
    result = prime * result + ((deviceTypeName == null) ? 0 : deviceTypeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DeviceType other = (DeviceType) obj;
    if (deviceModelName == null) {
      if (other.deviceModelName != null) return false;
    } else if (!deviceModelName.equals(other.deviceModelName)) return false;
    if (deviceTypeName == null) {
      if (other.deviceTypeName != null) return false;
    } else if (!deviceTypeName.equals(other.deviceTypeName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "DeviceType [deviceTypeName=" + deviceTypeName + ", devicePrefix=" + devicePrefix
        + ", deviceModelName=" + deviceModelName + ", parameterTypes=" + parameterTypes + "]";
  }
}
