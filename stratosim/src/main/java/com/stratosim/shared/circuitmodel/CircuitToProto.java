package com.stratosim.shared.circuitmodel;
//package com.stratosim.shared.circuitmodel;
//
//import com.stratosim.shared.devicemodel.DeviceType;
//import com.stratosim.shared.devicemodel.ParameterType;
//import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObjects.CircuitData;
//import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObjects.Device;
//import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObjects.DeviceTypeId;
//import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObjects.Id;
//import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObjects.Parameter;
//import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObjects.ParameterTypeId;
//import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObjects.Port;
//import com.stratosim.shared.model.circuit.impl.circuitobject.CircuitObjects.Wire;
//import com.stratosim.shared.model.circuit.impl.circuitobject.Files.CircuitFile;
//import com.stratosim.shared.model.circuit.impl.circuitobject.Files.FileKey;
//import com.stratosim.shared.model.circuit.impl.circuitobject.Files.SimulationSettings;
//import com.stratosim.shared.model.circuit.impl.circuitobject.Files.VersionKey;
//import com.stratosim.shared.model.circuit.impl.circuitobject.Files.SimulationSettings.TransientSettings;
//import com.stratosim.shared.model.circuit.impl.circuitobject.Files.ViewPort;
//import com.stratosim.shared.model.circuit.impl.circuitobject.IdFactory;
//import com.stratosim.shared.model.util.Point;
//import com.stratosim.shared.model.util.VersionId;
//import com.google.protobuf.ByteString;
//
//class CircuitToProto {
//  
//  private final Circuit circuit;
//  
//  CircuitToProto(Circuit circuit) {
//    this.circuit = circuit;
//  }
//  
//  CircuitFile toProto() {
//    throw new UnsupportedOperationException("incomplete!");
//  }
//  
//  private FileKey getFileKey() {
//    String fileKeyString = circuit.getFileKey().get();
//    return FileKey.newBuilder()
//        .setFileKey(fileKeyString).build();
//  }
//  
//  private VersionKey getVersionKey() {
//    String versionKeyString = circuit.getVersionKey().get();
//    return VersionKey.newBuilder().setVersionKey(versionKeyString).build();
//  }
//  
//  private SimulationSettings getSimulationSettings() {
//    return SimulationSettings.newBuilder()
//        .setType(SimulationSettings.Type.TRANSIENT)
//        .setTransientSettings(getTransientSettings())
//        .setVersion(VersionId.getDefaultInstance())
//        .build();
//  }
//  
//  private TransientSettings getTransientSettings() {
//    return TransientSettings.newBuilder()
//        .setDuration(circuit.getSimulationLength())
//        .build();
//  }
//  
//  private ViewPort getViewPort() {
//    return ViewPort.newBuilder()
//        .setScale(circuit.getScale())
//        .setPan(getPoint(circuit.getPan()))
//        .setVersion(VersionId.getDefaultInstance())
//        .build();
//  }
//  
//  private Point getPoint(com.stratosim.shared.circuitmodel.Point pt) {
//    return Point.newBuilder()
//        .setX(pt.getX())
//        .setY(pt.getY())
//        .build();
//  }
//  
//  private CircuitData getCircuitData() {
//    throw new UnsupportedOperationException("incomplete!");
//  }
//  
//  private Wire getWire(com.stratosim.shared.circuitmodel.Wire wire) {
//    Wire.Builder builder = Wire.newBuilder();
//    
//    builder.setId(getId());
//    for (com.stratosim.shared.circuitmodel.Point pt : wire.getPoints()) {
//      builder.addVertices(getPoint(pt));
//    }
//    builder.setVersion(VersionId.getDefaultInstance());
//    
//    return builder.build();
//  }
//  
//  private Device getDevice(com.stratosim.shared.circuitmodel.Device device) {
//    Device.Builder builder = Device.newBuilder();
//    
//    builder.setId(getId());
//    builder.setDeviceType(getDeviceTypeId(device.getType()));
//    for (com.stratosim.shared.circuitmodel.Parameter parameter : device.getParameters()) {
//      builder.addParameters(getParameter(parameter));
//    }
//    builder.setLocation(getPoint(device.getLocation()));
//    builder.setRotation(device.getRotation());
//    builder.setMirrored(device.getMirrored());
//    builder.setVersion(VersionId.getDefaultInstance());
//    
//    return builder.build();
//  }
//  
//  private DeviceTypeId getDeviceTypeId(DeviceType type) {
//    return DeviceTypeId.newBuilder()
//        .setName(type.getName())
//        .setModel(type.getModel())
//        .setVersion(VersionId.getDefaultInstance())
//        .build();
//  }
//  
//  private Parameter getParameter(com.stratosim.shared.circuitmodel.Parameter parameter) {
//    return Parameter.newBuilder()
//        .setId(getId())
//        .setParameterType(getParameterTypeId(parameter.getType()))
//        .setValue(parameter.getValue())
//        .setVersion(VersionId.getDefaultInstance())
//        .build();
//  }
//  
//  private ParameterTypeId getParameterTypeId(ParameterType type) {
//    return ParameterTypeId.newBuilder()
//        .setName(type.getName())
//        .setVersion(VersionId.getDefaultInstance())
//        .build();
//  }
//  
//  private Device getDevice(WireJunction junction) {
//    return Device.newBuilder()
//        .setId(getId())
//        .setDeviceType(getWireJunctionDeviceTypeId())
//        .setLocation(getPoint(junction.getLocation()))
//        .setRotation(junction.getRotation())
//        .clearMirrored()
//        .setVersion(VersionId.getDefaultInstance())
//        .build();
//  }
//  
//  private DeviceTypeId getWireJunctionDeviceTypeId() {
//    return DeviceTypeId.newBuilder()
//        .setName("wire-junction")
//        .clearModel()
//        .setVersion(VersionId.getDefaultInstance())
//        .build();
//  }
//  
//  private Port getPort(com.stratosim.shared.circuitmodel.Port port) {
//    return Port.newBuilder()
//        .setId(getId())
//        .setRelativeLocation(getPoint(port.getRelativeLocation()))
//        .setName(port.getName())
//        .setVersion(VersionId.getDefaultInstance())
//        .build();
//  }
//  
//  private Id getId() {
//    ByteString byteString = ByteString.copyFrom(IdFactory.newId().asBytes());
//    return Id.newBuilder().setData(byteString).build();
//  }
//
//}
