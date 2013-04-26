package com.stratosim.client.ui.devicepanel;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.widget.GridList;
import com.stratosim.shared.circuithelpers.DeviceManager;
import com.stratosim.shared.devicemodel.DeviceType;

// TODO(tpondich): Don't reference DeviceManager. Build using addDeviceLibrary calls.
public class LeftDevicePanel extends ResizeComposite implements HasChangeHandlers {

  private final static int BUTTON_SIZE = 40;

  private final GridList<String> categoryList;

  public LeftDevicePanel() {
    categoryList = new GridList<String>(1);
    categoryList.setStylePrimaryName("stratosim-DeviceList");

    DeviceManager deviceManager = StratoSimStatic.getLocalDeviceManager();

    for (String category : deviceManager.getCategories()) {
      DeviceType deviceType =
          deviceManager.getDeviceTypeModels(deviceManager.getDeviceTypes(category).get(0)).get(0);
      Image img = new DeviceTypeWidget(deviceType, BUTTON_SIZE, BUTTON_SIZE).getImage();
      categoryList.add(img, img, category, deviceType.getFullName(), category);
    }

    categoryList.setSelected(deviceManager.getCategories().get(0));

    categoryList.addChangeHandler(changeHandler);
    initWidget(categoryList);
  }

  /**
   * Update the icon of the currently selected button.
   * 
   * @param deviceType
   */
  public void setDeviceType(DeviceType deviceType) {
    Image img = new DeviceTypeWidget(deviceType, BUTTON_SIZE, BUTTON_SIZE).getImage();
    categoryList.replace(img, img, categoryList.getSelectedItem(), deviceType.getFullName(),
        categoryList.getSelectedItem(), categoryList.getSelectedIndex());
  }

  private ChangeHandler changeHandler = new ChangeHandler() {
    @Override
    public void onChange(ChangeEvent event) {
      fireChangeEvent();
    }
  };

  public String getSelected() {
    return categoryList.getSelectedItem();
  }

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return super.addHandler(handler, ChangeEvent.getType());
  }

  private void fireChangeEvent() {
    DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
  }
}
