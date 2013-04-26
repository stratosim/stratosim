package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.gwt.user.client.ui.Image;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.VersionByDateComparator;
import com.stratosim.shared.filemodel.VersionMetadata;

public class FileChooserState extends AbstractState {
  private final State previous;
  private final ImmutableList<VersionMetadata> fileList;
  private final ImmutableMap<FileKey, String> thumbnailDataMap;

  FileChooserState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager,
      ImmutableList<VersionMetadata> fileList, ImmutableMap<FileKey, String> thumbnailDataMap,
      State previous) {
    super(display, asyncManager);

    this.previous = checkNotNull(previous);
    this.fileList = checkNotNull(fileList);
    this.thumbnailDataMap = checkNotNull(thumbnailDataMap);
  }

  @Override
  public boolean isFileChooserVisible() {
    return true;
  }

  @Override
  public ImmutableSortedMap<VersionMetadata, Optional<Image>> getFileChooserList() {
    ImmutableSortedMap.Builder<VersionMetadata, Optional<Image>> builder =
        ImmutableSortedMap.orderedBy(Collections.reverseOrder(new VersionByDateComparator()));

    for (VersionMetadata file : fileList) {
      if (!file.getFileKey().equals(display.getCurrentCircuit().getFileKey())) {
        if (!thumbnailDataMap.containsKey(file.getFileKey())) {
          builder.put(file, Optional.<Image>absent());
        } else {
          builder.put(
              file,
              Optional.of(new Image("data:image/png;base64,"
                  + thumbnailDataMap.get(file.getFileKey()))));
        }
      }
    }

    return builder.build();
  }

  @Override
  public State goFileChooserChange(FileKey fileKey) {
    checkNotNull(fileKey);

    display.clearSimulationErrors();

    return new WaitForOpenFileState(display, asyncManager, previous, fileKey);
  }

  @Override
  public State goFileChooserClose() {
    return previous;
  }

}
