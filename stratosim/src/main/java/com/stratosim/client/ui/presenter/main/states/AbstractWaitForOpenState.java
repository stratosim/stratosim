package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.circuitmodel.Circuit;

public abstract class AbstractWaitForOpenState extends AbstractWaitState {

  private final State previous;
  private final State next;

  public AbstractWaitForOpenState(MainPresenter.Display display,
      MainPresenter.AsyncManager asyncManager, State previous, State next) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
    this.next = checkNotNull(next);
  }

  @Override
  public final void enter() {
    display.showWorkingMessage("Opening...");
    doOpen();
  }

  abstract protected void doOpen();

  @Override
  public State goOpenCallbackFailure() {
    display.showErrorMessage("Open Failed!");

    return previous;
  }

  @Override
  public State goOpenCallbackSuccess(Circuit circuit) {
    display.clearMessage();

    try {
      display.setCurrentCircuit(circuit);
    } catch (Exception e) {
      display.showErrorMessage("WARNING: Old Circuit Version");
    }

    return next;
  }

  @Override
  public State goOpenCallbackAccessException() {
    display.showErrorMessage("Access Denied!");

    return previous;
  }

  @Override
  public State goOpenCallbackPersistenceException() {
    display.showErrorMessage("File Not Found!");

    return previous;
  }
}
