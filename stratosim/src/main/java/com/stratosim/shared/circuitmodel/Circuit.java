package com.stratosim.shared.circuitmodel;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.stratosim.shared.circuithelpers.WireMerger;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class Circuit implements Serializable {
  private static final long serialVersionUID = -598990900069779193L;

  // This stuff is injected.
  // FileKey for each circuit file (not each version).
  // Corresponds to a name, but static during file renames.
  private FileKey fileKey = null;
  // VersionMetadataKey corresponds to a save instance
  private VersionMetadataKey versionKey = null;
  private FileRole fileRole = FileRole.NONE;

  // This stuff is actually persisted.
  private Map<String, Integer> deviceNumbers;

  private String name = "";

  private double scale = 0.8;
  private Point pan = new Point(0, 0); // In current scale

  // TODO(tpondich): OrderedSet ?
  private Map<PortOwnerId, Device> devices;
  private Map<PortOwnerId, WireJunction> wireJunctions;
  private Map<WireId, Wire> wires;

  private SimulationSettings simulationSettings = new SimulationSettings(true, "1e-3s", "1Hz",
      "100Hz");

  public Circuit() {
    devices = Maps.newHashMap();
    wires = Maps.newHashMap();
    wireJunctions = Maps.newHashMap();

    deviceNumbers = Maps.newHashMap();
  }

  /**
   * copy constructor -- does deep copy of devices and wires
   */
  private Circuit(Circuit circuit) {
    this();

    this.fileKey = circuit.fileKey;
    this.versionKey = circuit.versionKey;

    this.name = circuit.name;

    this.fileRole = circuit.fileRole;

    this.simulationSettings = circuit.simulationSettings;

    this.scale = circuit.scale;
    this.pan = circuit.pan;

    this.deviceNumbers = Maps.newHashMap(circuit.deviceNumbers);

    for (Map.Entry<PortOwnerId, Device> entry : circuit.devices.entrySet()) {
      PortOwnerId portOwnerId = entry.getKey();
      Device device = entry.getValue();

      Device newDevice =
          device.getType().create(device.getLocation(), device.getRotation(), device.isMirrored(),
              device.getParameter("Name").getValue());

      newDevice.setParameters(device.getParameters());
      
      for (Port port : device.getPorts()) {
        for (WireId wireId : port.getWireIds()) {
          newDevice.getPort(port.getName()).addWire(wireId);
        }
      }

      devices.put(portOwnerId, newDevice);
    }

    for (Map.Entry<PortOwnerId, WireJunction> entry : circuit.wireJunctions.entrySet()) {
      PortOwnerId portOwnerId = entry.getKey();
      WireJunction junction = entry.getValue();

      WireJunction newJunction = new WireJunction(junction.getOnlyPort().getLocation());
      wireJunctions.put(portOwnerId, newJunction);
      for (WireId wireId : junction.getOnlyPort().getWireIds()) {
        newJunction.getOnlyPort().addWire(wireId);
      }
    }

    // Wires are Immutable.
    wires = Maps.newHashMap(circuit.wires);
  }

  public int getAndIncrementDeviceNumber(String prefix) {
    if (!deviceNumbers.containsKey(prefix)) {
      deviceNumbers.put(prefix, 1);
    }

    int num = deviceNumbers.get(prefix);
    deviceNumbers.put(prefix, num + 1);
    return num;
  }

  public int getDeviceNumber(String prefix) {
    if (!deviceNumbers.containsKey(prefix)) {
      deviceNumbers.put(prefix, 1);
    }

    int num = deviceNumbers.get(prefix);
    return num;
  }

  // These functions should never return the same value twice. Ideally this
  // should only include information from the circuit graph and not anything
  // related to drawing.
  private String generateName(Device device) {
    // This is ugly. This had a reason in the beginning, but now it might as
    // well
    // just be numbered inside PortOwnerId.
    return "DEVICE_" + getAndIncrementDeviceNumber("DEVICE_");
  }

  private String generateName(WireJunction wireJunction) {
    // This is ugly. This had a reason in the beginning, but now it might as
    // well
    // just be numbered inside PortOwnerId.
    return "JUNCTION_" + getAndIncrementDeviceNumber("JUNCTION_");
  }

  /**
   * When a device is added, it should not be connected to the circuit in any way. Its name will be
   * dynamically assigned.
   */
  public PortOwnerId addDevice(Device d) {
    assert (!devices.containsValue(d));
    for (Port port : d.getPorts()) {
      assert (port.getWireIds().isEmpty());
    }

    PortOwnerId deviceId = new PortOwnerId(generateName(d));

    assert (!devices.containsKey(deviceId));
    devices.put(deviceId, d);

    return deviceId;
  }

  /**
   * When a wire is added, it should have references to ports to devices in the circuit. This method
   * will take care of linking the ports back to the wires.
   * 
   * @param wire
   */
  public WireId addWire(Wire wire) {
    assert (wire.getStartPortId() != null);
    assert (wire.getEndPortId() != null);

    Port startPort = getPort(wire.getStartPortId());
    Port endPort = getPort(wire.getEndPortId());
    assert (startPort != null);
    assert (endPort != null);

    assert (!wires.containsValue(wire));
    assert (wire.getPoints() != null);
    assert (!wire.getPoints().isEmpty());
    assert (startPort.getLocation().equals(wire.getPoints().get(0))) : "" + startPort.getLocation()
        + wire.getPoints().get(0);
    assert (endPort.getLocation().equals(wire.getPoints().get(wire.getPoints().size() - 1))) : ""
        + endPort.getLocation() + wire.getPoints().get(wire.getPoints().size() - 1);
    assert (devices.containsValue(startPort.getOwner()) || wireJunctions.containsValue(startPort
        .getOwner()));
    assert (devices.containsValue(endPort.getOwner()) || wireJunctions.containsValue(endPort
        .getOwner()));

    WireId wireId = new WireId(wire.getStartPortId(), wire.getEndPortId());
    assert (!wires.containsKey(wireId));

    wires.put(wireId, wire);

    startPort.addWire(wireId);
    endPort.addWire(wireId);

    return wireId;
  }

  /**
   * A safe way to replace a wire with a shadow wire when only a move occurs.
   * 
   * @param oldWire
   * @param newWire
   */
  public void replaceWire(WireId wireId, Wire newWire) {
    assert (wires.containsKey(wireId));
    Wire oldWire = wires.get(wireId);
    assert (oldWire.getStartPortId() == newWire.getStartPortId());
    assert (oldWire.getEndPortId() == newWire.getEndPortId());

    wires.put(wireId, newWire);
  }

  public void replaceDevice(PortOwnerId deviceId, Device newDevice) {
    assert (devices.containsKey(deviceId));
    Device oldDevice = devices.get(deviceId);
    for (Port port : oldDevice.getPorts()) {
      for (WireId wireId : port.getWireIds()) {
        assert (newDevice.getPort(port.getName()).getWireIds().contains(wireId));
      }
    }

    devices.put(deviceId, newDevice);
  }

  public PortOwnerId addWireJunction(WireJunction wj) {
    assert (!wireJunctions.containsValue(wj));
    assert (wj.getOnlyPort().getWireIds().isEmpty());
    // TODO(tpondich): Assert unique location.

    PortOwnerId wireJunctionId = new PortOwnerId(generateName(wj));

    wireJunctions.put(wireJunctionId, wj);

    return wireJunctionId;
  }

  public void removeDevice(PortOwnerId deviceId) {
    assert (devices.containsKey(deviceId));

    Device device = devices.get(deviceId);

    // TODO(tpondich): We probably don't want to delete the wires.
    // Create a dummy component with just one port.
    for (Port port : device.getPorts()) {
      for (WireId wireId : port.getWireIds()) {
        removeWire(wireId);
      }
    }

    devices.remove(deviceId);
  }

  /**
   * Remove a wire that's connected up in the circuit.
   * 
   * @param w
   */
  public void removeWire(WireId wireId) {
    removeWire(wireId, true);
  }

  /**
   * This is designed to be called from WireMerger and nothing else. Other classes should call
   * removeWire(Wire). Calling it with false will leave WireJunctions with 2 connections.
   * 
   * @param w
   * @param mergeWireJunction
   */
  public void removeWire(WireId wireId, boolean mergeWireJunction) {
    assert (wires.containsKey(wireId));

    Wire wire = wires.get(wireId);

    Port startPort = getPort(wire.getStartPortId());
    Port endPort = getPort(wire.getEndPortId());

    assert (startPort != null);
    assert (endPort != null);

    startPort.removeWire(wireId);
    endPort.removeWire(wireId);

    wires.remove(wireId);

    // TODO(tpondich): Make this part of the deleter?
    if (mergeWireJunction) {
      if (startPort.getOwner() instanceof WireJunction && startPort.getWireIds().size() == 2) {
        WireMerger merger = new WireMerger(getWireJunctionId((WireJunction) startPort.getOwner()));
        merger.commitToCircuit(this);
      }
      if (endPort.getOwner() instanceof WireJunction && endPort.getWireIds().size() == 2) {
        WireMerger merger = new WireMerger(getWireJunctionId((WireJunction) endPort.getOwner()));
        merger.commitToCircuit(this);
      }
    }
  }

  @Deprecated
  private PortOwnerId getWireJunctionId(WireJunction wireJunction) {
    // TODO(tpondich): Use BiMap ?
    PortOwnerId id = null;
    for (Map.Entry<PortOwnerId, WireJunction> entry : wireJunctions.entrySet()) {
      if (entry.getValue() == wireJunction) {
        id = entry.getKey();
      }
    }
    return id;
  }

  public void removeWireJunction(PortOwnerId wireJunctionId) {
    assert (wireJunctions.containsKey(wireJunctionId));
    assert (wireJunctions.get(wireJunctionId).getOnlyPort().getWireIds().isEmpty());

    wireJunctions.remove(wireJunctionId);
  }

  public Device getDevice(PortOwnerId deviceId) {
    if (deviceId == null) {
      return null;
    }

    return devices.get(deviceId);
  }

  public Wire getWire(WireId wireId) {
    if (wireId == null) {
      return null;
    }

    return wires.get(wireId);
  }

  public Port getPort(PortId portId) {
    if (portId == null) {
      return null;
    }

    Device device = devices.get(portId.getPortOwnerId());
    if (device != null) {
      return device.getPort(portId.getPortName());
    }

    WireJunction junction = wireJunctions.get(portId.getPortOwnerId());
    if (junction != null) {
      return junction.getOnlyPort();
    }

    return null;
  }

  public Label getLabel(LabelId labelId) {
    if (labelId == null) {
      return null;
    }

    Device device = devices.get(labelId.getPortOwnerId());
    if (device != null) {
      return device.getLabel();
    }

    return null;
  }

  public ImmutableList<Device> getDevices() {
    return ImmutableList.copyOf(devices.values());
  }

  public ImmutableSet<PortOwnerId> getDeviceIds() {
    return ImmutableSet.copyOf(devices.keySet());
  }

  public ImmutableList<Wire> getWires() {
    return ImmutableList.copyOf(wires.values());
  }

  public ImmutableList<WireJunction> getWireJunctions() {
    return ImmutableList.copyOf(wireJunctions.values());
  }

  public void setScale(double scale) {
    this.scale = scale;
  }

  public double getScale() {
    return scale;
  }

  public Point getPan() {
    return pan;
  }

  public void setPan(Point pan) {
    this.pan = pan;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSimulationSettings(SimulationSettings simulationSettings) {
    this.simulationSettings = simulationSettings;
  }

  public SimulationSettings getSimulationSettings() {
    return simulationSettings;
  }

  public FileKey getFileKey() {
    return fileKey;
  }

  public void setFileKey(FileKey key) {
    this.fileKey = key;
  }

  public VersionMetadataKey getVersionKey() {
    return versionKey;
  }

  public void setVersionKey(VersionMetadataKey key) {
    this.versionKey = key;
  }

  // TODO(tpondich): Serialize to protobuffer or some other data object.
  // TODO(tpondich): On failure(Device Manager changed too much), throw
  // exception.
  public Circuit snapshot() {
    return new Circuit(this);
  }

  private ImmutableList<Point> getAllPoints() {
    ImmutableList.Builder<Point> builder = ImmutableList.builder();
    for (Device device : devices.values()) {
      builder.add(device.getLocation().translate(-device.getWidth() / 2, -device.getHeight() / 2));
      builder.add(device.getLocation().translate(device.getWidth() / 2, device.getHeight() / 2));
      builder.add(device.getLabel().getCenterLocation()
          .translate(-device.getLabel().getWidth() / 2, -device.getLabel().getHeight() / 2));
      builder.add(device.getLabel().getCenterLocation()
          .translate(device.getLabel().getWidth() / 2, device.getLabel().getHeight() / 2));
    }
    for (Wire wire : wires.values()) {
      builder.addAll(wire.getPoints());
    }
    ImmutableList<Point> allObjects = builder.build();

    return allObjects;
  }

  public Point getTopLeft() {
    ImmutableList<Point> points = getAllPoints();
    if (points.isEmpty()) {
      return new Point(0, 0);
    }
    int minX = Collections.min(points, Point.COMPARE_BY_X).getX();
    int minY = Collections.min(points, Point.COMPARE_BY_Y).getY();
    return new Point(minX, minY);
  }

  public Point getBottomRight() {
    ImmutableList<Point> points = getAllPoints();
    if (points.isEmpty()) {
      return new Point(0, 0);
    }
    int maxX = Collections.max(points, Point.COMPARE_BY_X).getX();
    int maxY = Collections.max(points, Point.COMPARE_BY_Y).getY();
    return new Point(maxX, maxY);
  }

  public PortOwnerId getDeviceIdAt(Point mousePosition, int margin) {

    for (Map.Entry<PortOwnerId, Device> entry : devices.entrySet()) {
      if (entry.getValue().contains(mousePosition, margin)) {
        return entry.getKey();
      }
    }

    return null;
  }

  public Device getDeviceAt(Point mousePosition, int margin) {
    return getDevice(getDeviceIdAt(mousePosition, margin));
  }

  public WireId getWireIdAt(Point mousePosition, int margin) {

    for (Map.Entry<WireId, Wire> entry : wires.entrySet()) {
      if (entry.getValue().contains(mousePosition, margin)) {
        return entry.getKey();
      }
    }

    return null;
  }

  public Wire getWireAt(Point mousePosition, int margin) {
    return getWire(getWireIdAt(mousePosition, margin));
  }

  public PortId getPortIdAt(Point mousePosition, int margin) {

    PortOwnerId deviceId = getDeviceIdAt(mousePosition, margin);
    if (deviceId != null) {
      Device device = getDevice(deviceId);
      for (Port port : device.getPorts()) {
        if (port.contains(mousePosition, margin)) {
          return getPortIdOfDevicePort(deviceId, port.getName());
        }
      }
    }

    PortOwnerId wireJunctionId = getWireJunctionIdAt(mousePosition, margin);
    if (wireJunctionId != null) {
      return getPortIdOfWireJunctionId(wireJunctionId);
    }

    return null;
  }

  public Port getPortAt(Point mousePosition, int margin) {
    return getPort(getPortIdAt(mousePosition, margin));
  }

  public LabelId getLabelIdAt(Point mousePosition, int margin) {
    PortOwnerId deviceId = getDeviceIdAt(mousePosition, margin);

    if (deviceId != null) {
      Device device = getDevice(getDeviceIdAt(mousePosition, margin));
      Label label = device.getLabel();
      if (label.contains(mousePosition, margin)) {
        return getLabelIdOfDeviceId(deviceId);
      }
    }

    return null;
  }

  public Label getLabel(Point mousePosition, int margin) {
    return getLabel(getLabelIdAt(mousePosition, margin));
  }

  public PortOwnerId getWireJunctionIdAt(Point mousePosition, int margin) {
    for (Map.Entry<PortOwnerId, WireJunction> entry : wireJunctions.entrySet()) {
      if (entry.getValue().contains(mousePosition, margin)) {
        return entry.getKey();
      }
    }

    return null;
  }

  public WireJunction getWireJunctionAt(Point mousePosition, int margin) {
    return getWireJunction(getWireJunctionIdAt(mousePosition, margin));
  }

  public WireJunction getWireJunction(PortOwnerId wireJunctionId) {
    return wireJunctions.get(wireJunctionId);
  }

  public PortId getPortIdOfWireJunctionId(PortOwnerId wireJunctionId) {
    assert (wireJunctions.containsKey(wireJunctionId));

    return new PortId(wireJunctionId, wireJunctions.get(wireJunctionId).getOnlyPort().getName());
  }

  public PortId getPortIdOfDevicePort(PortOwnerId deviceId, String portName) {
    assert (devices.containsKey(deviceId));
    boolean isPortValid = false;
    for (Port port : devices.get(deviceId).getPorts()) {
      if (portName.equals(port.getName())) {
        isPortValid = true;
      }
    }
    assert (isPortValid);

    return new PortId(deviceId, portName);
  }

  public PortOwnerId getDeviceIdOfLabelId(LabelId labelId) {
    assert (devices.containsKey(labelId.getPortOwnerId()));

    return labelId.getPortOwnerId();
  }

  public LabelId getLabelIdOfDeviceId(PortOwnerId deviceId) {
    assert (devices.containsKey(deviceId));

    return new LabelId(deviceId);
  }

  public FileRole getFileRole() {
    return fileRole;
  }

  public void setFileRole(FileRole fileRole) {
    this.fileRole = fileRole;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((devices == null) ? 0 : devices.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((simulationSettings == null) ? 0 : simulationSettings.hashCode());
    result = prime * result + ((wireJunctions == null) ? 0 : wireJunctions.hashCode());
    result = prime * result + ((wires == null) ? 0 : wires.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Circuit other = (Circuit) obj;
    if (devices == null) {
      if (other.devices != null) return false;
    } else if (!devices.equals(other.devices)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (simulationSettings == null) {
      if (other.simulationSettings != null) return false;
    } else if (!simulationSettings.equals(other.simulationSettings)) return false;
    if (wireJunctions == null) {
      if (other.wireJunctions != null) return false;
    } else if (!wireJunctions.equals(other.wireJunctions)) return false;
    if (wires == null) {
      if (other.wires != null) return false;
    } else if (!wires.equals(other.wires)) return false;
    return true;
  }

  public String getMetadataString() {
    return "Name: " + name + " FileKey: " + fileKey + " VersionKey: " + versionKey + " fileRole: "
        + fileRole;
  }

  @Override
  public String toString() {
    String s = devices.size() + " " + wires.size() + " " + wireJunctions.size() + "\n";
    for (Wire wire : wires.values()) {
      s += wire + "\n";
    }
    for (Device device : devices.values()) {
      s += device + "\n";
    }
    return s;
  }
}
