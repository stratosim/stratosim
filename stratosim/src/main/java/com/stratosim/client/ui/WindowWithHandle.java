package com.stratosim.client.ui;

import com.google.gwt.core.client.JavaScriptObject;

public class WindowWithHandle {
  private JavaScriptObject window;

  public WindowWithHandle(String target) {
    openWindowJSNI(target);
  }
  
  public boolean isWindowOpen() {
    return isWindowOpenJSNI();
  }

  private native JavaScriptObject openWindowJSNI(String target) /*-{
	var window = $wnd.open(target, '_blank', '');
	this.@com.stratosim.client.ui.WindowWithHandle::window = window;
  }-*/;

  private native boolean isWindowOpenJSNI() /*-{
    var window = this.@com.stratosim.client.ui.WindowWithHandle::window;
    if (window && typeof(window) != 'undefined' && parseInt(window.innerWidth) != 0) {
      return true;
    } else {
      return false;
    }
  }-*/;
}
