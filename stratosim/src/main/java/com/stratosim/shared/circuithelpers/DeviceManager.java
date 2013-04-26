package com.stratosim.shared.circuithelpers;

import com.google.common.collect.ImmutableList;
import com.stratosim.shared.devicemodel.DeviceType;

public interface DeviceManager {
  ImmutableList<String> getCategories();

  String getDeviceDisplay(String category);

  ImmutableList<DeviceType> getDeviceTypes(String category);

  ImmutableList<DeviceType> getDeviceTypeModels(DeviceType deviceType);
}
