package com.stratosim.client.history;

import com.stratosim.shared.filemodel.VersionMetadataKey;

public interface SimulateVersion extends HistoryState {

  VersionMetadataKey getVersionKey();

}
