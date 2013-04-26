package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.FileVisibility;

public class WaitForGetFileVisibilityState extends AbstractWaitState {
  private final State previous;
  private final boolean previousIsSimulate;

  WaitForGetFileVisibilityState(MainPresenter.Display display,
      MainPresenter.AsyncManager asyncManager, State previous, boolean previousIsSimulate) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
    this.previousIsSimulate = previousIsSimulate;
  }

  @Override
  public void enter() {
    display.showWorkingMessage("Loading Sharing Settings...");
    asyncManager.fireGetFileVisibilityAsync(display.getCurrentCircuit().getFileKey());
  }

  @Override
  public State goGetPermissionsCallbackFailure() {
    display.showErrorMessage("Loading Sharing Settings Failed!");

    return previous;
  }

  @Override
  public State goGetPermissionsCallbackSuccess(FileVisibility fileVisibility) {
    display.clearMessage();

    return new ShareChooserState(display, asyncManager, previous, fileVisibility, previousIsSimulate);
  }
}
