package com.stratosim.client.filemanager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.XsrfProtectedServiceAsync;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public interface FileServiceAsync extends XsrfProtectedServiceAsync {

  void save(Circuit circuit, AsyncCallback<VersionMetadata> callback);
  
  void copy(Circuit copyCircuit, AsyncCallback<VersionMetadata> copyCallback);

  void open(VersionMetadataKey versionKey, AsyncCallback<Circuit> callback);
  
  void listLatest(AsyncCallback<ImmutableList<VersionMetadata>> callback);
  
  void listVersions(FileKey fileKey, AsyncCallback<ImmutableList<VersionMetadata>> listVersionsCallback);

  void getLatestThumbnails(AsyncCallback<ImmutableMap<FileKey, String>> callback);

  void setFileVisibility(FileKey fileKey, FileVisibility visibility, AsyncCallback<Void> callback);

  void getFileVisibility(FileKey fileKey, AsyncCallback<FileVisibility> callback);

  void openLatest(FileKey fileKey, AsyncCallback<Circuit> callback);

  void deleteFile(FileKey fileKey, AsyncCallback<Void> callback);

}
