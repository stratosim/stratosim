package com.stratosim.client.ui.presenter;

import com.google.gwt.user.client.ui.HasWidgets;

public interface Presenter {

  void go(HasWidgets container);

  /**
   * unregister all event handlers
   */
  void unregisterAll();
}
