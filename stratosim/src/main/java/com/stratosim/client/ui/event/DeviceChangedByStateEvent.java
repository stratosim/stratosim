package com.stratosim.client.ui.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.stratosim.shared.devicemodel.DeviceType;

public class DeviceChangedByStateEvent extends GwtEvent<DeviceChangedByStateEvent.Handler> {

  public static interface Handler extends EventHandler {
    void onDeviceSelected(DeviceChangedByStateEvent event);
  }

  public static final Type<Handler> TYPE = new Type<Handler>();

  public static HandlerRegistration register(EventBus eventBus, Handler handler) {
    return eventBus.addHandler(TYPE, handler);
  }

  private final DeviceType deviceType;

  public DeviceChangedByStateEvent(DeviceType deviceType) {
    this.deviceType = deviceType;
  }

  public DeviceType getDeviceType() {
    return deviceType;
  }

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onDeviceSelected(this);
  }

}
