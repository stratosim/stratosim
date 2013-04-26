package com.stratosim.client.ui.presenter.main.states;

import com.stratosim.client.ui.presenter.main.MainPresenter;

public class AbstractPanelSettingState extends AbstractState {

  public AbstractPanelSettingState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManger) {
    super(display, asyncManger);
  }
  
  @Override
  public final boolean doHidePanels() {
    return true;
  }
}
