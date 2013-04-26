package com.stratosim.server.persistence;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Blob;
import com.google.common.collect.ImmutableMap;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.DeletingWithCollaboratorsException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.RemovingLastOwnerException;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public interface UserPersistenceLayer {

  VersionMetadata putCircuit(Circuit circuit) throws PersistenceException, AccessException;

  Circuit getCircuit(VersionMetadataKey versionKey) throws PersistenceException, AccessException;

  Blob getCircuitDataFromVersion(VersionMetadataKey versionKey, DownloadFormat format)
      throws PersistenceException, AccessException;

  VersionMetadata getVersion(VersionMetadataKey versionKey) throws PersistenceException,
      AccessException;
  
  VersionMetadata getLatestVersion(FileKey fileKey) throws PersistenceException, AccessException;

  ImmutableMap<LowercaseEmailAddress, FileRole> getPermissions(FileKey fileKey) throws AccessException, PersistenceException;

  void updatePermissions(FileKey fileKey, ImmutableMap<LowercaseEmailAddress, FileRole> diff)
      throws RemovingLastOwnerException, AccessException, PersistenceException;

  ImmutableMap<LowercaseEmailAddress, FileRole> setPermissions(FileKey fileKey, ImmutableMap<LowercaseEmailAddress, FileRole> permissions)
      throws RemovingLastOwnerException, AccessException, PersistenceException;
  
  void deleteFile(FileKey fileKey) throws AccessException, DeletingWithCollaboratorsException, PersistenceException;
  
  void setPublic(FileKey fileKey, boolean isPublic) throws AccessException, PersistenceException;

  boolean isPublic(FileKey fileKey) throws AccessException, PersistenceException;

  CircuitHash getCircuitHash(VersionMetadataKey versionKey) throws AccessException, PersistenceException;

  List<VersionMetadata> getLatestVersions() throws PersistenceException;
  
  List<VersionMetadata> getVersions(FileKey fileKey) throws PersistenceException;

  Map<FileKey, String> getLatestThumbnails() throws PersistenceException;

}
