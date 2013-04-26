package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gwt.visualization.client.DataTable;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;

public class WaitForSimulateDataState extends AbstractWaitState {
  
  private final State previous;
  
  WaitForSimulateDataState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous) {
    super(display, asyncManager);
    
    this.previous = checkNotNull(previous);
  }

  @Override
  public void enter() {
    asyncManager.fireSimulationDatatableAsync(display.getCurrentCircuit().getVersionKey());
  }

  @Override
  public State goSimulationDatatableCallbackFailure() {
    // TODO(tpondich): Check what's in the message.
    // presenter.setSimulationErrors(message);
    display.showErrorMessage("Simulation Failed!");

    return previous;
  }

  @Override
  public State goSimulationDatatableCallbackSuccess(DataTable data) {

    // TODO(tpondich): Make setCurrentData validate, so exceptions can be caught here.
    display.setCurrentData(data);

    return new ChartFileState(display, asyncManager);
  }
}
