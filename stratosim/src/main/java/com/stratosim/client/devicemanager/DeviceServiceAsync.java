package com.stratosim.client.devicemanager;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.XsrfProtectedServiceAsync;
import com.stratosim.shared.devicemodel.DeviceLibrary;

public interface DeviceServiceAsync extends XsrfProtectedServiceAsync {
  void getDefaultLibrary(AsyncCallback<DeviceLibrary> asyncCallback);
}
