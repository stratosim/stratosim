package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.VersionMetadata;

public class WaitForListVersionsState extends AbstractWaitState {
  private final State previous;

  WaitForListVersionsState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      State previous) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
  }

  @Override
  public void enter() {
    display.showWorkingMessage("Loading Versions List...");
    asyncManager.fireListVersionsAsync(display.getCurrentCircuit().getFileKey());
  }

  @Override
  public State goListVersionsCallbackFailure() {
    display.showErrorMessage("Loading Version List Failed!");

    return previous;
  }

  @Override
  public State goListVersionsCallbackSuccess(ImmutableList<VersionMetadata> versionList) {
    display.clearMessage();
    return new VersionChooserState(display, asyncManager, versionList, previous);
  }

}
