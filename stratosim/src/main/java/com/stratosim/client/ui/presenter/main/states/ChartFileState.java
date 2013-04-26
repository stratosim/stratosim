package com.stratosim.client.ui.presenter.main.states;

import com.stratosim.client.history.HistoryState;
import com.stratosim.client.history.HistoryStateFactory;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;

public class ChartFileState extends AbstractChartState {

  ChartFileState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager) {
    super(display, asyncManager);
  }

  @Override
  protected State getSchematicState() {
    return new SchematicFileState(display, asyncManager);
  }

  @Override
  public HistoryState getURLState() {
    return HistoryStateFactory.simulateFile(display.getCurrentCircuit().getFileKey());
  }
}
