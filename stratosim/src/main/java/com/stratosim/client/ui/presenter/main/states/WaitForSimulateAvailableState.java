package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.DownloadFormat;

public class WaitForSimulateAvailableState extends AbstractPollingState {

  private final State next;

  public WaitForSimulateAvailableState(MainPresenter.Display display,
      MainPresenter.AsyncManager asyncManager, State next) {
    super(display, asyncManager);

    this.next = checkNotNull(next);
  }

  @Override
  protected void doPrepareForPolling() {
    display.showWorkingMessage("Simulating...");
  }

  @Override
  protected void doLaunchAsync() {
    asyncManager.fireDownloadAvailableAsync(display.getCurrentCircuit().getVersionKey(),
        DownloadFormat.CSV);
  }

  @Override
  public State goCircuitDownloadAvailableCallbackSuccess(boolean isAvailable) {
    if (isAvailable) {
      display.clearMessage();
      return new WaitForSimulateDataState(display, asyncManager, next);
      
    } else {
      if (super.tryKeepPolling()) {
        display.showWorkingMessage("Simulating...");
        return this;
        
      } else {
        display.showErrorMessage("Simulation Failed!");
        return next;
        
      }
      
    }
  }

  @Override
  public State goCircuitDownloadAvailableCallbackFailure() {
    display.showErrorMessage("Simulation Failed!");

    return next;
  }
}
