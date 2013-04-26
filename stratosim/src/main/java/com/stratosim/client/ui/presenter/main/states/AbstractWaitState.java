package com.stratosim.client.ui.presenter.main.states;

import com.stratosim.client.ui.presenter.main.MainPresenter;

public class AbstractWaitState extends AbstractState {

  public AbstractWaitState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManger) {
    super(display, asyncManger);
  }
  
  @Override
  public final boolean isDialogBackPaneVisible() {
    return true;
  }
}
