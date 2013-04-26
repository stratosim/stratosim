package com.stratosim.client.filemanager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.DeletingWithCollaboratorsException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

@RemoteServiceRelativePath("file-service")
public interface FileService extends XsrfProtectedService {

  VersionMetadata save(Circuit circuit) throws AccessException;
  
  VersionMetadata copy(Circuit circuit) throws AccessException;

  Circuit open(VersionMetadataKey versionKey) throws AccessException, PersistenceException;
  
  Circuit openLatest(FileKey fileKey) throws AccessException, PersistenceException;
  
  /**
   * Return a list of all the files accessible by a user sorted by date.  The latest VersionMetadata
   * of each file is returned.
   */
  ImmutableList<VersionMetadata> listLatest();
  
  ImmutableList<VersionMetadata> listVersions(FileKey fileKey);

  ImmutableMap<FileKey, String> getLatestThumbnails();
  
  /**
   * Update visibility for file {@code fileKey}. Overwrites all old permissions.
   * 
   * @param fileKey The file's key.
   * @param permissions New file visibility.
   * @throws AccessException If this user isn't allowed to edit the file's permissions.
   * @throws PersistenceException If the fileKey is not found.
   */
  void setFileVisibility(FileKey fileKey, FileVisibility visibility)
      throws AccessException, PersistenceException;
  
  /**
   * Get visibility for file {@code fileKey}.
   * 
   * @param fileKey The file's key.
   * @return FileVisibility.
   * @throws AccessException If this user isn't allowed to edit the file's permissions.
   * @throws PersistenceException  If the fileKey is not found.
   */
  FileVisibility getFileVisibility(FileKey fileKey) throws AccessException, PersistenceException;
  
  /**
   * Deletes a file. The file must have only 1 user, though, and that user must be owner.
   * 
   * @param fileKey The file.
   * @throws DeletingWithCollaboratorsException 
   */
  void deleteFile(FileKey fileKey) throws AccessException, DeletingWithCollaboratorsException;
}
