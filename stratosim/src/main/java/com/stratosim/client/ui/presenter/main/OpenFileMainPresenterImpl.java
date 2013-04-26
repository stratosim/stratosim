package com.stratosim.client.ui.presenter.main;

import com.stratosim.client.ui.presenter.main.states.SchematicNewState;
import com.stratosim.client.ui.presenter.main.states.WaitForOpenFileState;
import com.stratosim.shared.filemodel.FileKey;

public class OpenFileMainPresenterImpl extends AbstractMainPresenter {

  public OpenFileMainPresenterImpl(Display display, AsyncManager asyncManager, FileKey key) {
    super(display, asyncManager);

    setCurrentState(new WaitForOpenFileState(display, asyncManager, new SchematicNewState(display,
        asyncManager, true), key));
  }
}
