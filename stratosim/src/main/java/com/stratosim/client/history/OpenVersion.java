package com.stratosim.client.history;

import com.stratosim.shared.filemodel.VersionMetadataKey;

public interface OpenVersion extends HistoryState {

  VersionMetadataKey getVersionKey();

}
