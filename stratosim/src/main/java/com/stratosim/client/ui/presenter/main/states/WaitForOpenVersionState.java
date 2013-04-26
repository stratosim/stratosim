package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class WaitForOpenVersionState extends AbstractWaitForOpenState {

  private final VersionMetadataKey key;
  
  // This is designed for use with #simulate.  Do not call this and go to SchematicState.
  // Use the other constructor for that.
  public WaitForOpenVersionState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous, State next, VersionMetadataKey key) {
    super(display, asyncManager, previous, next);

    this.key = checkNotNull(key);
  }
  

  public WaitForOpenVersionState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous,
      VersionMetadataKey versionKey) {
    super(display, asyncManager, previous, new SchematicVersionState(display, asyncManager));

    this.key = versionKey;
  }

  @Override
  protected void doOpen() {
    asyncManager.fireOpenAsync(key);
  }
}
