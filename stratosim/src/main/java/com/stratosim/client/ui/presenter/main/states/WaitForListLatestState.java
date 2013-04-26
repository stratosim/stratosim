package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.VersionMetadata;

public class WaitForListLatestState extends AbstractWaitState {
  private final State previous;
  
  private ImmutableMap<FileKey, String> thumbnailDataMap = null;
  private ImmutableList<VersionMetadata> fileList = null;
  private boolean listLatestReturned = false;
  private boolean getLatestThumbnailsReturned = false;

  WaitForListLatestState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, State previous) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
  }

  @Override
  public void enter() {
    display.showWorkingMessage("Loading File List...");
    asyncManager.fireListLatestAsync();
    asyncManager.fireGetLatestThumbnailsAsync();
  }
  
  private State requestReturned() {
    if (!listLatestReturned || !getLatestThumbnailsReturned) {
      return this;
    }
    
    if (thumbnailDataMap != null && fileList != null) {
      display.clearMessage();
      return new FileChooserState(display, asyncManager, fileList, thumbnailDataMap, previous);
    } else {
      display.showErrorMessage("Loading File List Failed!");
      return previous;
    }
  }

  @Override
  public State goListLatestCallbackFailure() {
    listLatestReturned = true;

    return requestReturned();
  }
  
  @Override
  public State goListLatestCallbackSuccess(ImmutableList<VersionMetadata> fileList) {
    listLatestReturned = true;
    this.fileList = fileList;

    return requestReturned();
  }

  @Override
  public State goGetLatestThumbnailsCallbackSuccess(ImmutableMap<FileKey, String> thumbnailDataMap) {
    getLatestThumbnailsReturned = true;
    this.thumbnailDataMap = thumbnailDataMap;

    return requestReturned();
  }

  @Override
  public State goGetLatestThumbnailsCallbackFailure() {
    getLatestThumbnailsReturned = true;
    
    return requestReturned();
  }
}
