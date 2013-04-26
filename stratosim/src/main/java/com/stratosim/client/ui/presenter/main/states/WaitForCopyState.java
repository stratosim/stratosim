package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.VersionMetadata;

public class WaitForCopyState extends AbstractWaitState {
  private final State previous;
  private final State next;

  WaitForCopyState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous, State next) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
    this.next = checkNotNull(next);
  }

  @Override
  public void enter() {
    display.showWorkingMessage("Copying..."); 
    asyncManager.fireCopyAsync(display.getCurrentCircuit());
  }

  @Override
  public State goCopyCallbackSuccess(VersionMetadata metadata) {
    display.getCurrentCircuit().setFileKey(metadata.getFileKey());
    display.getCurrentCircuit().setVersionKey(metadata.getVersionKey());
    display.getCurrentCircuit().setFileRole(FileRole.OWNER);
    
    display.showDoneMessage("Copied!");

    return next;
  }

  @Override
  public State goCopyCallbackFailure() {
    display.showErrorMessage("Copying Failed!");

    return previous;
  }
}
