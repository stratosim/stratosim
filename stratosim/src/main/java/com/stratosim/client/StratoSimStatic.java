package com.stratosim.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.stratosim.client.devicemanager.DeviceService;
import com.stratosim.client.devicemanager.DeviceServiceAsync;
import com.stratosim.client.devicemanager.LocalDeviceManager;
import com.stratosim.client.filemanager.DownloadAvailableService;
import com.stratosim.client.filemanager.DownloadAvailableServiceAsync;
import com.stratosim.client.filemanager.FileService;
import com.stratosim.client.filemanager.FileServiceAsync;
import com.stratosim.client.filemanager.LocalFileManager;
import com.stratosim.client.filemanager.TriggerDownloadService;
import com.stratosim.client.filemanager.TriggerDownloadServiceAsync;
import com.stratosim.shared.DirectClientData;

public class StratoSimStatic {

  private StratoSimStatic() {
    throw new UnsupportedOperationException("uninstantiable");
  }

  private static final EventBus EVENT_BUS = new SimpleEventBus();
  private static final LocalFileManager LOCAL_FILE_MANAGER = LocalFileManager.INSTANCE;
  private static final LocalDeviceManager LOCAL_DEVICE_MANAGER = LocalDeviceManager.INSTANCE;

  private static FileServiceAsync FILE_SERVICE_ASYNC = null;
  private static DeviceServiceAsync DEVICE_SERVICE_ASYNC = null;
  private static DownloadAvailableServiceAsync CIRCUIT_DOWNLOAD_AVAILABLE_SERVICE_ASYNC =
      null;
  private static TriggerDownloadServiceAsync TRIGGER_DOWNLOAD_SERVICE_ASYNC =
      null;

  /**
   * initialize rpc services
   */
  public static void setXsrfToken(XsrfToken token) {
    FILE_SERVICE_ASYNC = (FileServiceAsync) GWT.create(FileService.class);
    setToken(token, FILE_SERVICE_ASYNC);
    DEVICE_SERVICE_ASYNC = (DeviceServiceAsync) GWT.create(DeviceService.class);
    setToken(token, DEVICE_SERVICE_ASYNC);
    CIRCUIT_DOWNLOAD_AVAILABLE_SERVICE_ASYNC =
        (DownloadAvailableServiceAsync) GWT.create(DownloadAvailableService.class);
    setToken(token, CIRCUIT_DOWNLOAD_AVAILABLE_SERVICE_ASYNC);
    TRIGGER_DOWNLOAD_SERVICE_ASYNC =
        (TriggerDownloadServiceAsync) GWT.create(TriggerDownloadService.class);
    setToken(token, TRIGGER_DOWNLOAD_SERVICE_ASYNC);
  }

  private static <S> void setToken(XsrfToken token, S service) {
    ((HasRpcToken) service).setRpcToken(token);
  }

  public static EventBus getEventBus() {
    return EVENT_BUS;
  }

  public static LocalFileManager getLocalFileManager() {
    return LOCAL_FILE_MANAGER;
  }

  public static LocalDeviceManager getLocalDeviceManager() {
    return LOCAL_DEVICE_MANAGER;
  }

  public static FileServiceAsync getFileService() {
    return FILE_SERVICE_ASYNC;
  }

  public static DeviceServiceAsync getDeviceService() {
    return DEVICE_SERVICE_ASYNC;
  }
  
  public static TriggerDownloadServiceAsync getTriggerDownloadService() {
    return TRIGGER_DOWNLOAD_SERVICE_ASYNC;
  }

  public static DownloadAvailableServiceAsync getCircuitDownloadAvailableService() {
    return CIRCUIT_DOWNLOAD_AVAILABLE_SERVICE_ASYNC;
  }
  
  // --------------------------------------------------------------------------
  // Direct client data
  // --------------------------------------------------------------------------
  
  private static final DirectClientData directClientData = fetchDirectData();
  
  private static DirectClientData fetchDirectData() {
    try {
      String encoded = getNativeString(DirectClientData.JS_NAME);
      SerializationStreamFactory factory = GWT.create(DirectData.class);
      return (DirectClientData) factory.createStreamReader(encoded).readObject();
    } catch (SerializationException ex) {
      throw new IllegalStateException("failed to get direct client data", ex);
    }
  }
  
  public static DirectClientData getDirectData() {
    return directClientData;
  }
  
  private static native String getNativeString(String name) /*-{
    return eval("$wnd." + name);
  }-*/;
}
