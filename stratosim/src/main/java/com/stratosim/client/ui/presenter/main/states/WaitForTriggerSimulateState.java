package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.DownloadFormat;

public class WaitForTriggerSimulateState extends AbstractWaitState {
  private final State previous;
  
  public WaitForTriggerSimulateState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous) {
    super(display, asyncManager);
    
    this.previous = checkNotNull(previous);
  }

  @Override
  public void enter() {
    display.clearSimulationErrors();
    display.showWorkingMessage("Simulating...");
    asyncManager.fireTriggerRenderAsync(display.getCurrentCircuit().getVersionKey(), DownloadFormat.CSV);
  }

  @Override
  public State goTriggerGenerateCallbackFailure() {
    display.showErrorMessage("Simulation Failed!");

    return previous;
  }

  @Override
  public State goTriggerGenerateCallbackSuccess(boolean isAvailable) {
    if (!isAvailable) {
      return new WaitForSimulateAvailableState(display, asyncManager, previous);
      
    } else {
      display.clearMessage();
      return new WaitForSimulateDataState(display, asyncManager, previous);
      
    }
  }
}
