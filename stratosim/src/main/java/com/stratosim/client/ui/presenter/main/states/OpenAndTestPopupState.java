package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.WindowWithHandle;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;

public class OpenAndTestPopupState extends AbstractWaitState {
  private final State next;

  private final String target;

  private WindowWithHandle window;

  OpenAndTestPopupState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      String target, State next) {
    super(display, asyncManager);

    this.next = checkNotNull(next);

    this.target = target;
  }

  @Override
  public void enter() {
    window = new WindowWithHandle(target);

    asyncManager.fireTimerAsync(500);
  }

  @Override
  public State goTimerCallback() {
    if (window.isWindowOpen()) {
      return next;
    } else {
      return new AlertState(
          display,
          asyncManager,
          "Popup Blocked",
          "It looks like a popup was blocked. Popups are needed for downloads to work. " +
          "Please enable popups and try downloading again.",
          next);
    }
  }
}
