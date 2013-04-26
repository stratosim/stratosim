package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.ShareURLHelper;
import com.stratosim.shared.filemodel.DownloadFormat;

public class WaitForDownloadAvailableState extends AbstractPollingState {
  private final DownloadFormat format;
  private final State next;

  public WaitForDownloadAvailableState(MainPresenter.Display display,
      MainPresenter.AsyncManager asyncManager, DownloadFormat format, State next) {
    super(display, asyncManager);

    this.format = checkNotNull(format);
    this.next = checkNotNull(next);
  }

  @Override
  protected void doPrepareForPolling() {
    display.showWorkingMessage("Generating...");
  }

  @Override
  protected void doLaunchAsync() {
    asyncManager.fireDownloadAvailableAsync(display.getCurrentCircuit().getVersionKey(), format);
  }

  @Override
  public State goCircuitDownloadAvailableCallbackSuccess(boolean isAvailable) {
    if (isAvailable) {
      display.clearMessage();
      
      return new OpenAndTestPopupState(display, asyncManager,
          ShareURLHelper
              .getCircuitDownloadServiceUrl(display.getCurrentCircuit().getVersionKey(), format),
          next);

    } else {
      if (super.tryKeepPolling()) {
        display.showWorkingMessage("Generating...");
        return this;
      } else {
        display.showErrorMessage("Generation Failed!");
        return next;
      }
    }
  }

  @Override
  public State goCircuitDownloadAvailableCallbackFailure() {
    display.showErrorMessage("Generation Failed!");

    return next;
  }
}
