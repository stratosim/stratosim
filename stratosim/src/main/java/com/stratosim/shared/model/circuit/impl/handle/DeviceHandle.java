package com.stratosim.shared.model.circuit.impl.handle;

import com.stratosim.shared.model.circuit.Device;
import com.stratosim.shared.model.circuit.impl.circuitobject.AbstractCircuitObject;
import com.stratosim.shared.model.circuit.impl.data.ImmutableDeviceData;
import com.stratosim.shared.model.device.DeviceTypeId;
import com.stratosim.shared.model.drawing.Drawer;
import com.stratosim.shared.model.util.CircuitCoord;
import com.stratosim.shared.model.util.CircuitCoordOffset;

public class DeviceHandle extends AbstractCircuitObject implements Device {

  // All members must be handled appropriately in the shadow and copy sections below
  private ImmutableDeviceData data;
  
  public DeviceHandle(ImmutableDeviceData data) {
    this.data = data;
  }
  
  // ---------------------------------------------------------------------------------------------
  // Device data
  // ---------------------------------------------------------------------------------------------
  
  @Override
  public void setRotation(int rotation) {
    data = data.newCopyBuilder().setRotation(rotation).build();
  }

  @Override
  public void flipHorizontal() {
    data = data.newCopyBuilder().toggleMirrored().build();
  }

  @Override
  public void flipVertical() {
    int rotation = (data.getRotation() + 2) % 4;
    data = data.newCopyBuilder().toggleMirrored().setRotation(rotation).build();
  }

  @Override
  public void translate(CircuitCoordOffset offset) {
    CircuitCoord location = data.getLocation().add(offset);
    data = data.newCopyBuilder().setLocation(location).build();
  }

  @Override
  public DeviceTypeId getDeviceTypeId() {
    // TODO Auto-generated method stub
    return null;
  }

  // ---------------------------------------------------------------------------------------------
  // Drawing
  // ---------------------------------------------------------------------------------------------
  
  @Override
  public boolean isHidden() {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public void hide() {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public void unhide() {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public Drawer asDrawer() {
    throw new UnsupportedOperationException("not yet implemented");
  }

  // ---------------------------------------------------------------------------------------------
  // Shadow
  // ---------------------------------------------------------------------------------------------
  
  @Override
  public Device newCopy() {
    return new DeviceHandle(data);
  }

  // ---------------------------------------------------------------------------------------------
  // Shadow
  // ---------------------------------------------------------------------------------------------
  
  @Override
  public Shadow newShadow() {
    return new ShadowImpl(data);
  }
  
  private class ShadowImpl extends DeviceHandle implements Shadow {

    public ShadowImpl(ImmutableDeviceData data) {
      super(data);
    }

    @Override
    public void commit() {
      DeviceHandle.this.data = data;
    }
    
  }

}
