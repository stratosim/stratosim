package com.stratosim.client.ui.devicepanel;

import java.util.Collection;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.widget.GridList;
import com.stratosim.shared.circuithelpers.DeviceManager;
import com.stratosim.shared.devicemodel.DeviceType;

public class SingleDeviceSublist extends DeviceSublist {
  private GridList<DeviceType> subDeviceList;

  private DeviceType selectedDeviceType;

  public SingleDeviceSublist(Collection<DeviceType> deviceTypes) {
    DeviceType deviceType = deviceTypes.iterator().next();
    subDeviceList = new GridList<DeviceType>(2);

    // TODO(tpondich): Pass in instead of referencing deviceManager.
    DeviceManager deviceManager = StratoSimStatic.getLocalDeviceManager();

    for (DeviceType deviceTypeModel : deviceManager.getDeviceTypeModels(deviceType)) {
      subDeviceList.add(deviceTypeModel.getModel(), deviceTypeModel);
    }
    initWidget(subDeviceList);
    subDeviceList.setSelectedIndex(0);
    selectedDeviceType = subDeviceList.getSelectedItem();

    subDeviceList.addChangeHandler(changeHandler);
  }

  private ChangeHandler changeHandler = new ChangeHandler() {
    @Override
    public void onChange(ChangeEvent event) {
      selectedDeviceType = subDeviceList.getSelectedItem();
      fireChangeEvent();
    }
  };

  @Override
  public DeviceType getSelected() {
    return selectedDeviceType;
  }
}
