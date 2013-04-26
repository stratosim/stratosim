package com.stratosim.shared.devicemodel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.stratosim.client.devicemanager.DeviceService;
import com.stratosim.client.devicemanager.DeviceServiceAsync;
import com.stratosim.shared.devicemodel.DeviceLibrary;

public class GwtTestDeviceService extends GWTTestCase {
  
  public void testSerialization() {
    DeviceServiceAsync service = GWT.create(DeviceService.class);
    service.getDefaultLibrary(new AsyncCallback<DeviceLibrary>() {
      @Override
      public void onSuccess(DeviceLibrary lib) {
        finishTest();
      }
      
      @Override
      public void onFailure(Throwable arg0) {
        fail("RPC failed");
      }
    });
    delayTestFinish(1000);
  }

  @Override
  public String getModuleName() {
    return "com.stratosim.StratoSim";
  }
}
