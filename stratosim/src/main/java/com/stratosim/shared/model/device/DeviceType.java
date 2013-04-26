package com.stratosim.shared.model.device;


public interface DeviceType {

  String getName();

  String hasModel();

  String getModel();
  
  String getFullName();

  String getDevicePrefix();

  // Spice

  String getSpiceTemplate();

  // Drawing
  
  String getLabelTemplate();
  
  String getDrawCommands();

  int getWidth();
  
  int getHeight();
  
  // Custom types
  
  DeviceType newCustomCopy();
}
