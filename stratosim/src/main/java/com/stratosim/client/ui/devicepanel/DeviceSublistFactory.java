package com.stratosim.client.ui.devicepanel;

import java.util.Collection;

import com.stratosim.shared.devicemodel.DeviceType;

public class DeviceSublistFactory {
  public static DeviceSublist create(String display, Collection<DeviceType> deviceTypes) {
    if (display.equals("Single")) {
      return new SingleDeviceSublist(deviceTypes);
    } else if (display.equals("MultiSingle")) {
      return new MultiDeviceSingleParamSublist(deviceTypes);
    } else if (display.equals("MultiMulti")) {
      return new MultiDeviceMultiParamSublist(deviceTypes);
    } else {
      throw new IllegalArgumentException(display);
    }
  }
}
