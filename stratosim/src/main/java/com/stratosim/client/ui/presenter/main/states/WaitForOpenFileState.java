package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.FileKey;

public class WaitForOpenFileState extends AbstractWaitForOpenState {

  private final FileKey key;

  public WaitForOpenFileState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManger, State previous, State next, FileKey fileKey) {
    super(display, asyncManger, previous, next);

    this.key = checkNotNull(fileKey);
  }
  
  public WaitForOpenFileState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous, FileKey fileKey) {
    super(display, asyncManager, previous, new SchematicFileState(display, asyncManager));

    this.key = checkNotNull(fileKey);
  }

  @Override
  protected void doOpen() {
    asyncManager.fireOpenAsync(key);
  }
}
