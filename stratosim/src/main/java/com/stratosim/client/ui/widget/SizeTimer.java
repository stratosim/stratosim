package com.stratosim.client.ui.widget;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Hack to get around these 0 offset size issues.
 * 
 * @author tarun
 * 
 */
public class SizeTimer extends Timer {
  private Widget watchWidget;
  private int POLL_DELAY = 50;

  SizeTimer(Widget watchWidget) {
    this.watchWidget = watchWidget;
    this.cancel();
    this.schedule(POLL_DELAY);
  }

  @Override
  public void run() {
    if (watchWidget.getOffsetHeight() == 0 || watchWidget.getOffsetWidth() == 0) {
      this.schedule(POLL_DELAY);
    }
    ((RequiresResize) watchWidget).onResize();
  }
}
