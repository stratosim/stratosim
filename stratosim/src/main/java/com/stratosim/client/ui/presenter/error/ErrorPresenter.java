package com.stratosim.client.ui.presenter.error;

import com.stratosim.client.ui.presenter.Presenter;

public interface ErrorPresenter extends Presenter {

  interface Display extends com.stratosim.client.ui.presenter.Display<ErrorPresenter> {
    void setErrorMessageText(String text);
  }

}
