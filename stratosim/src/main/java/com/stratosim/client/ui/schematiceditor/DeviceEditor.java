package com.stratosim.client.ui.schematiceditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

class DeviceEditor extends Editor {
  public enum DeviceEditorAction implements EditorAction {
    ROTATE_RIGHT, ROTATE_LEFT, FLIP_HORIZONTAL, FLIP_VERTICAL
  }

  private static DeviceEditorUiBinder uiBinder = GWT.create(DeviceEditorUiBinder.class);

  interface DeviceEditorUiBinder extends UiBinder<Widget, DeviceEditor> {}

  public DeviceEditor() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("rotateLeft")
  void onRotateLeftClick(ClickEvent e) {
    fireValueChangeEvent(DeviceEditorAction.ROTATE_LEFT);
  }

  @UiHandler("rotateRight")
  void onRotateRightClick(ClickEvent e) {
    fireValueChangeEvent(DeviceEditorAction.ROTATE_RIGHT);
  }

  @UiHandler("flipHorizontal")
  void onflipHorizontalClick(ClickEvent e) {
    fireValueChangeEvent(DeviceEditorAction.FLIP_HORIZONTAL);
  }

  @UiHandler("flipVertical")
  void onflipVerticalClick(ClickEvent e) {
    fireValueChangeEvent(DeviceEditorAction.FLIP_VERTICAL);
  }

  @UiHandler("rotateLeft")
  void onRotateLeftFocus(FocusEvent e) {
    rejectFocus();
  }

  @UiHandler("rotateRight")
  void onRotateRightFocus(FocusEvent e) {
    rejectFocus();
  }

  @UiHandler("flipHorizontal")
  void onflipHorizontalFocus(FocusEvent e) {
    rejectFocus();
  }

  @UiHandler("flipVertical")
  void onflipVerticalFocus(FocusEvent e) {
    rejectFocus();
  }
}
