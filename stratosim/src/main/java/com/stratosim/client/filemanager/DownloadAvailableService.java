package com.stratosim.client.filemanager;


import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadataKey;

@RemoteServiceRelativePath("download-available-service")
public interface DownloadAvailableService extends XsrfProtectedService {
  boolean isAvailable(VersionMetadataKey versionKey, DownloadFormat format);
}
