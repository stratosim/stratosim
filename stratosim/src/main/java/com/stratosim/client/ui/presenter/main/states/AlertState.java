package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;

public class AlertState extends AbstractState {
  private final State next;
  
  private final String caption;
  private final String message;

  AlertState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      String caption, String message, State next) {
    super(display, asyncManager);

    this.caption = checkNotNull(caption);
    this.message = checkNotNull(message);
    
    this.next = checkNotNull(next);
  }
  
  public void enter() {
    display.showAlert(caption, message);
  }

  @Override
  public State goAlertDialogChange() {
    return next;
  }

}
