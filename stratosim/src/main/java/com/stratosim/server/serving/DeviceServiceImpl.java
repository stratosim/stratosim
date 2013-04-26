package com.stratosim.server.serving;

import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.stratosim.client.devicemanager.DeviceService;
import com.stratosim.server.DeviceManagerInstance;
import com.stratosim.shared.devicemodel.DeviceLibrary;

public class DeviceServiceImpl extends XsrfProtectedServiceServlet implements DeviceService {
  private static final long serialVersionUID = 9122697734176539795L;

  @Override
  public DeviceLibrary getDefaultLibrary() {
    return DeviceManagerInstance.INSTANCE.getDefaultLibrary();
  }
}
