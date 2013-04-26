package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.circuitmodel.Circuit;

public class WaitForDeleteState extends AbstractWaitState {
  private final State previous;

  WaitForDeleteState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      State previous) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
  }

  @Override
  public void enter() {
    display.showWorkingMessage("Deleting...");
    asyncManager.fireDeleteAsync(display.getCurrentCircuit().getFileKey());
  }

  @Override
  public State goDeleteCallbackSuccess() {
    display.showDoneMessage("Deleted!");

    // TODO(tpondich): Create constant name in LocalFileManager.
    Circuit defaultCircuit = StratoSimStatic.getLocalFileManager().create("Untitled");
    display.setCurrentCircuit(defaultCircuit);

    return new SchematicNewState(display, asyncManager);
  }

  @Override
  public State goDeleteCallbackFailure() {
    display.showErrorMessage("Delete Failed!");

    return previous;
  }

  @Override
  public State goDeleteCallbackDeletingWithCollaboratorsException() {
    display.showErrorMessage("Delete Failed!");

    return new AlertState(display, asyncManager, "Delete Failed",
        "Files can only be deleted if they have no collaborators. Please remove all collaborators using the Share Dialog and then delete the file.", previous);
  }
}
