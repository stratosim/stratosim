package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class VersionChooserState extends AbstractState {
  private final State previous;
  private final ImmutableList<VersionMetadata> versionList;

  VersionChooserState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      ImmutableList<VersionMetadata> fileList, State previous) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
    this.versionList = checkNotNull(fileList);
  }

  @Override
  public boolean isVersionChooserVisible() {
    return true;
  }

  @Override
  public ImmutableList<VersionMetadata> getVersionChooserList() {
    return versionList;
  }

  @Override
  public State goVersionChooserChange(VersionMetadataKey versionKey) {
    checkNotNull(versionKey);

    // Why is this here?
    display.clearSimulationErrors();

    return new WaitForOpenVersionState(display, asyncManager, previous, versionKey);
  }

  @Override
  public State goVersionChooserClose() {
    return previous;
  }

}
