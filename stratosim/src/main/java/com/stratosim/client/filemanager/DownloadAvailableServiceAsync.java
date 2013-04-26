package com.stratosim.client.filemanager;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.XsrfProtectedServiceAsync;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public interface DownloadAvailableServiceAsync extends XsrfProtectedServiceAsync {
  void isAvailable(VersionMetadataKey versionKey, DownloadFormat format, AsyncCallback<Boolean> callback);
}
