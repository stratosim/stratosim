package com.stratosim.client.devicemanager;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;
import com.stratosim.shared.devicemodel.DeviceLibrary;

@RemoteServiceRelativePath("device-service")
public interface DeviceService extends XsrfProtectedService {
  DeviceLibrary getDefaultLibrary();
}
