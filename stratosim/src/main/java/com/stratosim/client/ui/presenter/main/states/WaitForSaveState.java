package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.VersionMetadata;

public class WaitForSaveState extends AbstractWaitState {
  private final State previous;
  private final State next;

  WaitForSaveState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous, State next) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
    this.next = checkNotNull(next);
  }

  @Override
  public void enter() {
    display.showWorkingMessage("Saving...");
    asyncManager.fireSaveAsync(display.getCurrentCircuit());
  }

  @Override
  public State goSaveCallbackFailure() {
    display.showErrorMessage("Save Failed!");

    return previous;
  }

  @Override
  public State goSaveCallbackSuccess(VersionMetadata metadata) {
    display.getCurrentCircuit().setFileKey(metadata.getFileKey());
    display.getCurrentCircuit().setVersionKey(metadata.getVersionKey());
    
    display.showDoneMessage("Saved!");

    return next;
  }
}
