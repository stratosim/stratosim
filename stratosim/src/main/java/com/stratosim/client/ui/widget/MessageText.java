package com.stratosim.client.ui.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MessageText extends Composite {

  @UiField
  Label label;
  
  private static MessageTextUiBinder uiBinder = GWT.create(MessageTextUiBinder.class);
  interface MessageTextUiBinder extends UiBinder<Widget, MessageText> {}
  
  private Timer resetTimer;

  public final static int INFO = 0;
  public final static int WORKING = 1;
  public final static int DONE = 2;
  public final static int ERROR = 3;

  public MessageText() {
    initWidget(uiBinder.createAndBindUi(this));
    
    resetTimer = new ResetTimer();
  }

  public void setStylePrimaryName(String style) {
    super.setStylePrimaryName(style);
    label.setStylePrimaryName(style);
  }

  public synchronized void showMessage(String text, int millis, int severity) {
    resetTimer.cancel();

    label.setText(text);

    label.removeStyleDependentName("info");
    label.removeStyleDependentName("working");
    label.removeStyleDependentName("done");
    label.removeStyleDependentName("error");

    if (severity == INFO) {
      label.addStyleDependentName("info");
    } else if (severity == WORKING) {
      label.addStyleDependentName("working");
    } else if (severity == DONE) {
      label.addStyleDependentName("done");
    } else if (severity == ERROR) {
      label.addStyleDependentName("error");
    } else {
      assert false;
    }

    resetTimer.schedule(millis);
  }

  private class ResetTimer extends Timer {

    @Override
    public void run() {
      label.setText("");

      label.removeStyleDependentName("info");
      label.removeStyleDependentName("working");
      label.removeStyleDependentName("done");
      label.removeStyleDependentName("error");
    }
  }

  public void clearMessage() {
    resetTimer.cancel();
    
    label.setText("");

    label.removeStyleDependentName("info");
    label.removeStyleDependentName("working");
    label.removeStyleDependentName("done");
    label.removeStyleDependentName("error");
  }
}
