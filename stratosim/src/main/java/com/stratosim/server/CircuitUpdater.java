package com.stratosim.server;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.Device;
import com.stratosim.shared.circuitmodel.Port;
import com.stratosim.shared.circuitmodel.PortOwnerId;
import com.stratosim.shared.circuitmodel.WireId;
import com.stratosim.shared.devicemodel.DeviceType;

public class CircuitUpdater {

  // Current Circuit Version: 2

  // Delta From 0:
  // Voltage and Current Probes have the color and group parameters
  // added.
  private static DeviceType voltageProbeTypeV1 = null;
  private static DeviceType currentProbeTypeV1 = null;
  
  // Delta From 1:
  // All the devices have the new types injected
  // Most devices need rotation to be decremented by 1.

  static {
    for (DeviceType t : DeviceManagerInstance.INSTANCE.getDeviceTypes("Probes")) {
      if (t.getName().equals("Voltage Probe")) {
        voltageProbeTypeV1 = t;
      } else if (t.getName().equals("Current Probe")) {
        currentProbeTypeV1 = t;
      }
    }

    checkNotNull(voltageProbeTypeV1);
    checkNotNull(currentProbeTypeV1);
  }

  private CircuitUpdater() {
    throw new UnsupportedOperationException("not instantiable");
  }

  public static final void update(Circuit circuit) {
    updateFrom0To1(circuit);
    updateFrom1To2(circuit);
  }
  
  private static void updateFrom0To1(Circuit circuit) {
    final String[] COLORS = {"red", "orange", "yellow", "green", "blue", "purple"};
    int i = 0;

    for (PortOwnerId deviceId : circuit.getDeviceIds()) {
      Device device = circuit.getDevice(deviceId);
      
      if (device.getType().getName().equals("Voltage Probe")) {
        if (device.getParameters().size() >= 3) {
          continue;
          
        } else {
          Device newDevice =
              DeviceManagerInstance.INSTANCE
                  .getDeviceTypeModels(voltageProbeTypeV1)
                  .get(0)
                  .create(device.getLocation(), device.getRotation(), device.isMirrored(),
                      device.getParameter("Name").getValue());

          newDevice.setParameter("Group", "voltage1");
          newDevice.setParameter("Color", COLORS[i++ % COLORS.length]);
          
          for (Port port : device.getPorts()) {
            for (WireId wireId : port.getWireIds()) {
              newDevice.getPort(port.getName()).addWire(wireId);
            }
          }
          
          circuit.replaceDevice(deviceId, newDevice);
        }

      } else if (device.getType().getName().equals("Current Probe")) {
        if (device.getParameters().size() >= 3) {
          continue;
          
        } else {
          Device newDevice =
              DeviceManagerInstance.INSTANCE
                  .getDeviceTypeModels(currentProbeTypeV1)
                  .get(0)
                  .create(device.getLocation(), device.getRotation(), device.isMirrored(),
                      device.getParameter("Name").getValue());

          newDevice.setParameter("Group", "current1");
          newDevice.setParameter("Color", COLORS[i++ % COLORS.length]);
          
          for (Port port : device.getPorts()) {
            for (WireId wireId : port.getWireIds()) {
              newDevice.getPort(port.getName()).addWire(wireId);
            }
          }
          
          circuit.replaceDevice(deviceId, newDevice);
        }

      }
    }
  }
  
  private static void updateFrom1To2(Circuit circuit) {
    Device testVersionDevice = circuit.getDevices().get(0);
    if (testVersionDevice.getType().getPostScriptImage() != null) {
      // This circuit has already been updated.
      return;
    }

    for (PortOwnerId deviceId : circuit.getDeviceIds()) {
      Device device = circuit.getDevice(deviceId);

      if (!device.getType().getName().equals("Ground")) {
        if (device.isMirrored()) {
          device.rotateLeft();
        } else {
          device.rotateRight();
        }
      }

      device.setType(findModel(findDeviceType(device.getType().getName()), device.getType().getModel()));

    }
  }
  
  private static DeviceType findModel(DeviceType customDevice, String modelName) {
    if (modelName == null) {
      return customDevice;
    }
    
    for (DeviceType type : DeviceManagerInstance.INSTANCE.getDeviceTypeModels(customDevice)) {
      if (modelName.equals(type.getModel())) {
        return type;
      }
    }

    return customDevice;
  }
  
  private static DeviceType findDeviceType(String typeName) {
    for (String category : DeviceManagerInstance.INSTANCE.getCategories()) {
      for (DeviceType deviceType : DeviceManagerInstance.INSTANCE.getDeviceTypes(category)) {
        if (deviceType.getName().equals(typeName)) {
          return deviceType;
        } else if (deviceType.getName().equals(typeName.replace(" Source", ""))) {
          // Really Old Devices may have "Source" appended to their name.
          return deviceType;
        }
      }
    }

    throw new IllegalStateException("Device Not Found: " + typeName);
  }
}
