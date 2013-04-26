package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;

public class ConfirmationState extends AbstractState {
  private final State onYes;
  private final State onNo;
  
  private final String caption;
  private final String message;

  ConfirmationState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      String caption, String message, State onYes, State onNo) {
    super(display, asyncManager);

    this.caption = checkNotNull(caption);
    this.message = checkNotNull(message);
    
    this.onYes = checkNotNull(onYes);
    this.onNo = checkNotNull(onNo);
  }
  
  public final void enter() {
    display.showConfirmation(caption, message);
  }

  @Override
  public final State goConfirmationDialogChange(boolean isYes) {
    if (isYes) {
      return onYes;
    } else {
      return onNo;
    }
  }
}
