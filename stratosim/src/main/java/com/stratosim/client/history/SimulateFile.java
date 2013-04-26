package com.stratosim.client.history;

import com.stratosim.shared.filemodel.FileKey;

public interface SimulateFile extends HistoryState {

  FileKey getFileKey();

}
