package com.stratosim.client.ui.widget;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

// TODO(tpondich): UI Binder

public class FramePanel extends ResizeComposite
    implements
      HasCloseHandlers<FramePanel>,
      HasOneWidget,
      AcceptsOneWidget,
      HasWidgets {
  private static FramePanelUiBinder uiBinder = GWT.create(FramePanelUiBinder.class);

  interface FramePanelUiBinder extends UiBinder<Widget, FramePanel> {}
  
  @UiField
  LayoutPanel mainLayout;
  @UiField
  Label titleLabel;
  @UiField
  PushButton closeButton;
  @UiField
  SimpleLayoutPanel container;

  public FramePanel(String title, Widget innerWidget) {
    create(title, innerWidget);
  }

  public FramePanel() {
    create("", null);
  }

  private void create(String title, Widget innerWidget) {
    initWidget(uiBinder.createAndBindUi(this));
    
    titleLabel.setText(title);
    
    if (innerWidget != null) {
      container.setWidget(innerWidget);
    }

    setStylePrimaryName("stratosim-FramePanel");
  }

  @Override
  public void setWidget(IsWidget widget) {
    container.setWidget(widget);
  }

  @Override
  public void setWidget(Widget widget) {
    container.setWidget(widget);
  }

  @Override
  public Widget getWidget() {
    return container.getWidget();
  }

  public void setTitleLabelText(String text) {
    titleLabel.setText(text);
  }

  public void setShowCloseButton(boolean show) {
    closeButton.setVisible(show);
    if (!show) {
      closeButton.setPixelSize(0, 0);
    }
  }

  public void setStylePrimaryName(String styleName) {
    closeButton.removeStyleName(getStylePrimaryName() + "-CloseButton");

    super.setStylePrimaryName(styleName);

    closeButton.addStyleName(getStylePrimaryName() + "-CloseButton");

    titleLabel.setStylePrimaryName(getStylePrimaryName() + "-TitleLabel");
    container.setStylePrimaryName(getStylePrimaryName() + "-InnerPanel");
    mainLayout.setStylePrimaryName(getStylePrimaryName());
  }

  @UiHandler("closeButton")
  public void onClick(ClickEvent event) {
    fireCloseEvent();
  }

  public HandlerRegistration addCloseHandler(CloseHandler<FramePanel> handler) {
    return super.addHandler(handler, CloseEvent.getType());
  }

  private void fireCloseEvent() {
    FramePanelCloseEvent event = new FramePanelCloseEvent(this);
    fireEvent(event);
  }

  class FramePanelCloseEvent extends CloseEvent<FramePanel> {
    public FramePanelCloseEvent(FramePanel widget) {
      super(widget, false);
    }
  }

  @Override
  public void add(Widget w) {
    container.add(w);
  }

  @Override
  public void clear() {
    container.clear();
  }

  @Override
  public Iterator<Widget> iterator() {
    return container.iterator();
  }

  @Override
  public boolean remove(Widget w) {
    return container.remove(w);
  }
}
