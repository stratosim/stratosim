package com.stratosim.client.ui.presenter.main.states;

import com.google.gwt.user.client.Timer;
import com.stratosim.client.ui.presenter.main.MainPresenter;

public abstract class AbstractPollingState extends AbstractWaitState {
  private final static int INITIAL_DELAY = 1500;
  private final static int POLL_DELAY = 1500;
  private int retries = 6;

  private boolean isInitial = true;

  public AbstractPollingState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager) {
    super(display, asyncManager);
  }

  @Override
  public final void enter() {
    doPrepareForPolling();
    
    doPoll();

    isInitial = false;
  }
  
  private final void doPoll() {
    Timer timer = new Timer() {
      @Override
      public void run() {
        doLaunchAsync();
      }
    };
    timer.schedule(isInitial ? INITIAL_DELAY : POLL_DELAY);
  }

  protected abstract void doPrepareForPolling();
  protected abstract void doLaunchAsync();

  protected final boolean tryKeepPolling() {
    if (retries != 0) {
      retries--;
      doPoll();
      return true;
    } else {
      return false;
    }
  }
}
