package com.stratosim.client.ui.presenter.error;

import com.google.gwt.user.client.ui.HasWidgets;
import com.stratosim.client.ui.presenter.AbstractPresenter;

public class ErrorPresenterImpl extends AbstractPresenter implements ErrorPresenter {

  private final ErrorPresenter.Display display;

  public ErrorPresenterImpl(ErrorPresenter.Display display, String text) {
    this.display = display;
    display.setErrorMessageText(text);
  }

  @Override
  public void go(HasWidgets container) {
    container.clear();
    container.add(display.asWidget());
  }

}
