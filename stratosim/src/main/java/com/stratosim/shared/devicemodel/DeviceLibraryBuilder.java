package com.stratosim.shared.devicemodel;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class DeviceLibraryBuilder {
  private ImmutableList.Builder<String> categories;
  private ImmutableListMultimap.Builder<String, DeviceType> categoryToDeviceTypeNamesMap;
  private ImmutableMap.Builder<String, String> categoryToDisplayMap;
  private ImmutableListMultimap.Builder<DeviceType, DeviceType> deviceTypeCustomToDeviceTypeModelsMap;

  private Map<DeviceType, DeviceType> dedupDeviceType = Maps.newHashMap();

  public DeviceLibraryBuilder() {
    categories = new ImmutableList.Builder<String>();
    categoryToDeviceTypeNamesMap = new ImmutableListMultimap.Builder<String, DeviceType>();
    categoryToDisplayMap = new ImmutableMap.Builder<String, String>();
    deviceTypeCustomToDeviceTypeModelsMap =
        new ImmutableListMultimap.Builder<DeviceType, DeviceType>();
  }

  public void addDeviceType(DeviceType deviceType) {
    assert (deviceType.hasModel());
    DeviceType custom = deviceType.getCustom();
    if (!dedupDeviceType.containsKey(custom)) {
      dedupDeviceType.put(custom, custom);
    }
    deviceTypeCustomToDeviceTypeModelsMap.put(dedupDeviceType.get(custom), deviceType);
  }

  public void addCategory(String category, Collection<DeviceType> deviceTypes, String display) {
    for (DeviceType deviceType : deviceTypes) {
      assert (dedupDeviceType.containsKey(deviceType));
    }
    categoryToDeviceTypeNamesMap.putAll(category, deviceTypes);
    categoryToDisplayMap.put(category, display);
    categories.add(category);
  }

  public DeviceLibrary build() {
    return new DeviceLibrary(categories.build(), categoryToDeviceTypeNamesMap.build(),
        categoryToDisplayMap.build(), deviceTypeCustomToDeviceTypeModelsMap.build());
  }
}
