package com.stratosim.client.history;

import com.stratosim.shared.filemodel.FileKey;

public interface OpenFile extends HistoryState {

  FileKey getFileKey();

}
