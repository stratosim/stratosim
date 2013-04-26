package com.stratosim.client.ui.devicepanel;

import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.stratosim.shared.devicemodel.DeviceType;

public class SingleSublistDisplay extends ResizeComposite
    implements
      DeviceSublistDisplay,
      HasChangeHandlers {
  private final SimpleLayoutPanel panel;

  private DeviceSublist currentSublist;
  private final Map<String, DeviceSublist> sublists;

  public SingleSublistDisplay(Map<String, DeviceSublist> sublists) {
    this.panel = new SimpleLayoutPanel();
    this.sublists = sublists;

    for (DeviceSublist sublist : sublists.values()) {
      sublist.addChangeHandler(changeHandler);
    }

    initWidget(panel);
  }

  @Override
  public void showSublist(String category) {
    panel.clear();
    currentSublist = sublists.get(category);
    panel.add(currentSublist);
  }

  @Override
  public void hideSublist(String category) {
    currentSublist = null;
    panel.clear();
  }

  public DeviceType getSelected() {
    return currentSublist.getSelected();
  }

  private ChangeHandler changeHandler = new ChangeHandler() {
    @Override
    public void onChange(ChangeEvent event) {
      fireChangeEvent();
    }
  };

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return super.addHandler(handler, ChangeEvent.getType());
  }

  private void fireChangeEvent() {
    DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
  }
}
