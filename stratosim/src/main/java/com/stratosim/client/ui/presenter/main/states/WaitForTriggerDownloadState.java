package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.ShareURLHelper;
import com.stratosim.shared.filemodel.DownloadFormat;

public class WaitForTriggerDownloadState extends AbstractWaitState {
  private final DownloadFormat format;

  private final State next;

  WaitForTriggerDownloadState(MainPresenter.Display display,
      MainPresenter.AsyncManager asyncManager, DownloadFormat format, State next) {
    super(display, asyncManager);

    this.format = checkNotNull(format);
    this.next = checkNotNull(next);
  }

  @Override
  public void enter() {
    display.showWorkingMessage("Generating...");
    asyncManager.fireTriggerRenderAsync(display.getCurrentCircuit().getVersionKey(), format);
  }

  @Override
  public State goTriggerGenerateCallbackFailure() {
    display.showErrorMessage("Generation Failed!");

    return next;
  }

  @Override
  public State goTriggerGenerateCallbackSuccess(boolean isReady) {
    if (!isReady) {
      return new WaitForDownloadAvailableState(display, asyncManager, format, next);

    } else {
      display.clearMessage();
      
      return new OpenAndTestPopupState(display, asyncManager,
          ShareURLHelper
              .getCircuitDownloadServiceUrl(display.getCurrentCircuit().getVersionKey(), format),
          next);
    }
  }
}
