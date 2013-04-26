package com.stratosim.client.ui.devicepanel;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.widget.GridList;
import com.stratosim.shared.circuithelpers.DeviceManager;
import com.stratosim.shared.devicemodel.DeviceType;

public class MultiDeviceSingleParamSublist extends DeviceSublist {
  // TODO(tpondich): Flag duplicated code from MultiDeviceMultiParam
  private final static int BUTTON_SIZE = 40;
  private final static int DEVICE_LIST_COLS = 3;
  private final static int BUTTON_MARGIN = 10;
  private final static int BUTTON_LABEL = 20;

  private GridList<DeviceType> deviceList;
  private GridList<Integer> subDeviceList;

  private DeviceType selected;

  public MultiDeviceSingleParamSublist(Collection<DeviceType> deviceTypes) {
    deviceList = new GridList<DeviceType>(DEVICE_LIST_COLS);
    deviceList.setStylePrimaryName("stratosim-DeviceList");

    // TODO(tpondich): Flag duplicated code from MultiDeviceMultiParam
    LayoutPanel panel = new LayoutPanel();
    int deviceListHeight =
        (deviceTypes.size() / DEVICE_LIST_COLS + 1)
            * (BUTTON_SIZE + 2 * BUTTON_MARGIN + BUTTON_LABEL);
    if (deviceTypes.size() % DEVICE_LIST_COLS == 0) {
      deviceListHeight -= (BUTTON_SIZE + 2 * BUTTON_MARGIN + BUTTON_LABEL);
    }
    panel.add(deviceList);
    panel.setWidgetLeftRight(deviceList, 0, Unit.PX, 0, Unit.PX);
    panel.setWidgetTopHeight(deviceList, 0, Unit.PX, deviceListHeight, Unit.PX);
    subDeviceList = new GridList<Integer>(2);
    panel.add(subDeviceList);
    panel.setWidgetLeftRight(subDeviceList, 0, Unit.PX, 0, Unit.PX);
    panel.setWidgetTopBottom(subDeviceList, deviceListHeight, Unit.PX, 0, Unit.PX);

    // TODO(tpondich): Pass in instead of referencing deviceManager.
    DeviceManager deviceManager = StratoSimStatic.getLocalDeviceManager();

    for (DeviceType deviceType : deviceTypes) {
      Image img = new DeviceTypeWidget(deviceType, BUTTON_SIZE, BUTTON_SIZE).getImage();
      deviceList.add(img, img, deviceType.getName(), deviceType.getName(), deviceType);
    }

    DeviceType firstDeviceType = deviceTypes.iterator().next();
    int index = 0;
    for (DeviceType deviceTypeModel : deviceManager.getDeviceTypeModels(firstDeviceType)) {
      subDeviceList.add(deviceTypeModel.getModel(), index);
      index++;
    }

    initWidget(panel);
    deviceList.setSelectedIndex(0);
    subDeviceList.setSelectedIndex(0);
    selected =
        deviceManager.getDeviceTypeModels(deviceList.getSelectedItem()).get(
            subDeviceList.getSelectedIndex());

    deviceList.addChangeHandler(changeHandler);
    subDeviceList.addChangeHandler(changeHandler);
  }

  private ChangeHandler changeHandler = new ChangeHandler() {
    @Override
    public void onChange(ChangeEvent event) {
      DeviceManager deviceManager = StratoSimStatic.getLocalDeviceManager();
      selected =
          deviceManager.getDeviceTypeModels(deviceList.getSelectedItem()).get(
              subDeviceList.getSelectedIndex());
      fireChangeEvent();
    }
  };

  public DeviceType getSelected() {
    return selected;
  }
}
