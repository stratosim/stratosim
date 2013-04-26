package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.FileVisibility;

public class WaitForSetFileVisibilityState extends AbstractWaitState {
  private final State previous;
  private final FileVisibility fileVisibility;

  WaitForSetFileVisibilityState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous,
      FileVisibility fileVisibility) {
    super(display, asyncManager);
    
    this.previous = checkNotNull(previous);

    this.fileVisibility = checkNotNull(fileVisibility);
  }

  @Override
  public void enter() {
    display.showWorkingMessage("Updating Sharing Settings...");
    asyncManager.fireSetFileVisibilityAsync(display.getCurrentCircuit().getFileKey(), fileVisibility);
  }

  @Override
  public State goSetPermissionsCallbackFailure() {
    display.showErrorMessage("Updating Sharing Settings Failed!");

    return previous;
  }

  @Override
  public State goSetPermissionsCallbackSuccess() {
    display.showDoneMessage("Updated Sharing Settings");

    return previous;
  }
}
