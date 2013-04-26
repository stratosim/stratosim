package com.stratosim.client.ui.presenter.loading;

import com.google.gwt.user.client.ui.HasWidgets;
import com.stratosim.client.ui.presenter.AbstractPresenter;

public class LoadingPresenterImpl extends AbstractPresenter implements LoadingPresenter {

  private final LoadingPresenter.Display display;

  public LoadingPresenterImpl(LoadingPresenter.Display display) {
    this.display = display;
  }

  @Override
  public void go(HasWidgets container) {
    container.clear();
    container.add(display.asWidget());
  }

}
