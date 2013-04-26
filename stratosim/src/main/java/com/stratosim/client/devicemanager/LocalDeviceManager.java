package com.stratosim.client.devicemanager;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.shared.circuithelpers.DeviceManager;
import com.stratosim.shared.devicemodel.DeviceLibrary;
import com.stratosim.shared.devicemodel.DeviceType;

public enum LocalDeviceManager implements DeviceManager {

  INSTANCE;

  private static DeviceLibrary defaultLibrary;

  private boolean isReady = false;

  public void initialize() {
    StratoSimStatic.getDeviceService().getDefaultLibrary(initializeCallback);
  }

  private AsyncCallback<DeviceLibrary> initializeCallback = new AsyncCallback<DeviceLibrary>() {
    @Override
    public void onFailure(Throwable caught) {
      // TODO(tpondich): Retry or do something useful.
      throw new IllegalStateException(caught.getMessage());
    }

    @Override
    public void onSuccess(DeviceLibrary result) {
      defaultLibrary = result;
      isReady = true;
    }
  };

  public boolean isReady() {
    return isReady;
  }

  @Override
  public ImmutableList<String> getCategories() {
    Preconditions.checkState(isReady);
    return defaultLibrary.getCategories();
  }

  @Override
  public ImmutableList<DeviceType> getDeviceTypes(String category) {
    Preconditions.checkState(isReady);
    return defaultLibrary.getDeviceTypes(category);
  }

  @Override
  public String getDeviceDisplay(String category) {
    Preconditions.checkState(isReady);
    return defaultLibrary.getDeviceDisplay(category);
  }

  @Override
  public ImmutableList<DeviceType> getDeviceTypeModels(DeviceType deviceType) {
    Preconditions.checkState(isReady);
    return defaultLibrary.getDeviceTypeModels(deviceType);
  }
}
