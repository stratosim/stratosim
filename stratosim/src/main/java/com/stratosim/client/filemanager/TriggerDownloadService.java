package com.stratosim.client.filemanager;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.VersionMetadataKey;

@RemoteServiceRelativePath("trigger-download-service")
public interface TriggerDownloadService extends XsrfProtectedService {

  boolean trigger(VersionMetadataKey versionKey, DownloadFormat format) throws AccessException, PersistenceException;
}
