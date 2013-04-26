package com.stratosim.client.ui.presenter;

import com.google.gwt.user.client.ui.IsWidget;

public interface Display<T extends Presenter> extends IsWidget {
  void setPresenter(T presenter);
}
