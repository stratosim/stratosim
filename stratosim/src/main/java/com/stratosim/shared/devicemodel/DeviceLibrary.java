package com.stratosim.shared.devicemodel;

import java.io.Serializable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

// Immutable
public class DeviceLibrary implements Serializable {
  private static final long serialVersionUID = -6025021146382153503L;

  // Annoying workaround for Serialization whitelist and interfaces
  // Better workarounds are here:
  // http://groups.google.com/group/google-web-toolkit/browse_thread/thread/9eb513e449be3940
  @SuppressWarnings("unused")
  private DeviceType damnYouGWT;

  private/* final */ImmutableList<String> categories;
  private/* final */ImmutableListMultimap<String, DeviceType> categoryToDeviceTypesMap;
  private/* final */ImmutableMap<String, String> categoryToDisplayMap;
  private/* final */ImmutableListMultimap<DeviceType, DeviceType> deviceTypeCustomToDeviceTypeModelsMap;

  // For GWT Serialization
  @SuppressWarnings("unused")
  private DeviceLibrary() {}

  public DeviceLibrary(ImmutableList<String> categories,
      ImmutableListMultimap<String, DeviceType> categoryToDeviceTypesMap,
      ImmutableMap<String, String> categoryToDisplayMap,
      ImmutableListMultimap<DeviceType, DeviceType> deviceTypeCustomToDeviceTypeModelsMap) {
    // These are all ImmutableMaps of Immutable types so no copies are
    // necessary.
    this.categories = categories;
    this.categoryToDeviceTypesMap = categoryToDeviceTypesMap;
    this.categoryToDisplayMap = categoryToDisplayMap;
    this.deviceTypeCustomToDeviceTypeModelsMap = deviceTypeCustomToDeviceTypeModelsMap;
  }

  public ImmutableList<String> getCategories() {
    return categories;
  }

  public ImmutableList<DeviceType> getDeviceTypes(String category) {
    return categoryToDeviceTypesMap.get(category);
  }

  public ImmutableList<DeviceType> getDeviceTypeModels(DeviceType deviceTypeCustom) {
    return deviceTypeCustomToDeviceTypeModelsMap.get(deviceTypeCustom);
  }

  public String getDeviceDisplay(String category) {
    return categoryToDisplayMap.get(category);
  }
}
