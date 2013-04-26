package com.stratosim.client.ui.devicepanel;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.widget.GridList;
import com.stratosim.shared.circuithelpers.DeviceManager;
import com.stratosim.shared.devicemodel.DeviceType;

public class MultiDeviceMultiParamSublist extends DeviceSublist {
  private final static int BUTTON_SIZE = 40;
  private final static int DEVICE_LIST_COLS = 3;
  private final static int BUTTON_MARGIN = 10;
  private final static int BUTTON_LABEL = 20;

  private GridList<DeviceType> deviceList;
  private SimpleLayoutPanel paramPanel;

  private DeviceType selectedDeviceType;
  private Map<DeviceType, GridList<DeviceType>> subDeviceListMap = Maps.newHashMap();

  public MultiDeviceMultiParamSublist(Collection<DeviceType> deviceTypes) {
    deviceList = new GridList<DeviceType>(DEVICE_LIST_COLS);
    deviceList.setStylePrimaryName("stratosim-DeviceList");

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
    paramPanel = new SimpleLayoutPanel();
    panel.add(paramPanel);
    panel.setWidgetLeftRight(paramPanel, 0, Unit.PX, 0, Unit.PX);
    panel.setWidgetTopBottom(paramPanel, deviceListHeight, Unit.PX, 0, Unit.PX);

    // TODO(tpondich): Pass in instead of referencing deviceManager.
    DeviceManager deviceManager = StratoSimStatic.getLocalDeviceManager();

    for (DeviceType deviceType : deviceTypes) {
      GridList<DeviceType> subDeviceList = new GridList<DeviceType>(2);
      for (DeviceType deviceTypeModel : deviceManager.getDeviceTypeModels(deviceType)) {
        subDeviceList.add(deviceTypeModel.getModel(), deviceTypeModel);
      }
      if (subDeviceList.getItemCount() == 1) {
        subDeviceList.setVisible(false);
      }

      subDeviceList.setSelectedIndex(0, true);
      subDeviceList.addChangeHandler(modelChangeHandler);
      subDeviceListMap.put(deviceType, subDeviceList);

      Image img =
          new DeviceTypeWidget(subDeviceList.getSelectedItem(), BUTTON_SIZE, BUTTON_SIZE)
              .getImage();
      DeviceType firstDeviceType = subDeviceList.getSelectedItem();
      deviceList.add(img, img, deviceType.getName(), firstDeviceType.getFullName(), deviceType);
    }

    initWidget(panel);
    deviceList.setSelectedIndex(0);
    paramPanel.setWidget(subDeviceListMap.get(deviceTypes.iterator().next()));

    selectedDeviceType = subDeviceListMap.get(deviceList.getSelectedItem()).getSelectedItem();

    deviceList.addChangeHandler(deviceChangeHandler);
  }

  private ChangeHandler deviceChangeHandler = new ChangeHandler() {
    @Override
    public void onChange(ChangeEvent event) {
      GridList<DeviceType> subDeviceList = subDeviceListMap.get(deviceList.getSelectedItem());
      selectedDeviceType = subDeviceList.getSelectedItem();
      paramPanel.clear();
      paramPanel.setWidget(subDeviceList);
      fireChangeEvent();
    }
  };

  private ChangeHandler modelChangeHandler = new ChangeHandler() {
    @Override
    public void onChange(ChangeEvent event) {
      GridList<DeviceType> subDeviceList = subDeviceListMap.get(deviceList.getSelectedItem());
      selectedDeviceType = subDeviceList.getSelectedItem();

      Image img = new DeviceTypeWidget(selectedDeviceType, BUTTON_SIZE, BUTTON_SIZE).getImage();
      deviceList.replace(img, img, selectedDeviceType.getName(), selectedDeviceType.getFullName(),
          deviceList.getSelectedItem(), deviceList.getSelectedIndex());

      fireChangeEvent();
    }
  };

  public DeviceType getSelected() {
    return selectedDeviceType;
  }
}
