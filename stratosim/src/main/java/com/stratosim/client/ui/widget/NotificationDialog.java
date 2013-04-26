package com.stratosim.client.ui.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class NotificationDialog extends ResizeComposite implements HasValueChangeHandlers<String> {

  @UiField
  Label message;
  @UiField
  DockLayoutPanel buttonPanel;

  @UiField
  Image iconImage;

  @UiField
  ImageResource warningImage;
  @UiField
  ImageResource errorImage;

  public final static int WARNING = 0;
  public final static int ERROR = 1;

  // TODO(tpondich): UiBinder!
  private final static int BUTTON_WIDTH = 80;

  private static NotificationDialogUiBinder uiBinder = GWT.create(NotificationDialogUiBinder.class);

  interface NotificationDialogUiBinder extends UiBinder<Widget, NotificationDialog> {}

  public NotificationDialog() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setNotification(String text, int type, String... options) {
    message.setText(text);

    switch (type) {
      case WARNING:
        iconImage.setResource(warningImage);
        break;
      case ERROR:
        iconImage.setResource(errorImage);
        break;
    }

    buttonPanel.clear();

    for (String option : options) {
      final String caption = option;
      PushButton pb = new PushButton(caption);
      pb.addStyleName("stratosim-NotificationDialog-button");
      buttonPanel.addEast(pb, BUTTON_WIDTH);
      pb.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          fireValueChangeEvent(caption);
        }
      });
    }

    buttonPanel.add(new Label());
  }

  private void fireValueChangeEvent(String value) {
    ValueChangeEvent.fire(this, value);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
    return super.addHandler(handler, ValueChangeEvent.getType());
  }
}
