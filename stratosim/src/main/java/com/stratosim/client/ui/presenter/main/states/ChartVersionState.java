package com.stratosim.client.ui.presenter.main.states;

import com.stratosim.client.history.HistoryState;
import com.stratosim.client.history.HistoryStateFactory;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;

public class ChartVersionState extends AbstractChartState {

  ChartVersionState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager) {
    super(display, asyncManager);
  }
  
  @Override
  protected State getSchematicState() {
    return new SchematicVersionState(display, asyncManager);
  }

  @Override
  public HistoryState getURLState() {
    return HistoryStateFactory.simulateVersion(display.getCurrentCircuit().getVersionKey());
  }
}
