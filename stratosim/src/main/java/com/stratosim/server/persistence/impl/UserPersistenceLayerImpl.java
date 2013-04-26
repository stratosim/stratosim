package com.stratosim.server.persistence.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Blob;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.stratosim.server.persistence.UserPersistenceLayer;
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
import com.stratosim.shared.filemodel.VersionByDateComparator;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class UserPersistenceLayerImpl extends RealPersistenceLayerWrapper
    implements
      UserPersistenceLayer {

  private final UserAuthorizationChecker checker;
  private final LowercaseEmailAddress user;

  public UserPersistenceLayerImpl(LowercaseEmailAddress user) {
    this.user = user;
    this.checker = new UserAuthorizationChecker(getReal(), getHelper(), user);
  }

  @Override
  public VersionMetadata putCircuit(Circuit circuit) throws PersistenceException, AccessException {

    checker.checkWrite(circuit);

    return getReal().putCircuit(circuit, user);
  }

  @Override
  public Circuit getCircuit(VersionMetadataKey versionKey) throws PersistenceException,
      AccessException {

    checker.checkRead(versionKey);
    Circuit circuit = getReal().getCircuit(versionKey);

    // TODO(joshuan): Wasted datastore op?
    // TODO(joshuan): Should this be done in a separate rpc call from the client?
    circuit.setFileRole(checker.getFileRole(versionKey.getFileKey()));

    return circuit;
  }

  @Override
  public Blob getCircuitDataFromVersion(VersionMetadataKey versionKey, DownloadFormat format)
      throws PersistenceException, AccessException {

    checker.checkRead(versionKey);

    return getReal().getCircuitDataFromVersion(versionKey, format);
  }

  @Override
  public VersionMetadata getVersion(VersionMetadataKey versionKey) throws PersistenceException,
      AccessException {

    checker.checkRead(versionKey);

    return getReal().getVersion(versionKey);
  }

  @Override
  public VersionMetadata getLatestVersion(FileKey fileKey) throws PersistenceException,
      AccessException {

    checker.checkRead(fileKey);

    return getReal().getLatestVersion(fileKey);
  }

  @Override
  public List<VersionMetadata> getLatestVersions() throws PersistenceException {
    ImmutableMap<FileKey, VersionMetadata> latestVersions = getReal().getLatestVersions(user);

    List<VersionMetadata> files = Lists.newArrayList();
    for (VersionMetadata version : latestVersions.values()) {
      files.add(version);
    }

    Collections.sort(files, new VersionByDateComparator());
    Collections.reverse(files);

    return files;
  }

  @Override
  public List<VersionMetadata> getVersions(FileKey fileKey) throws PersistenceException {
    ImmutableList<VersionMetadata> versions = getReal().getVersions(fileKey);

    List<VersionMetadata> files = Lists.newArrayList();
    for (VersionMetadata version : versions) {
      files.add(version);
    }

    Collections.sort(files, new VersionByDateComparator());
    Collections.reverse(files);

    return files;
  }

  @Override
  public Map<FileKey, String> getLatestThumbnails() throws PersistenceException {
    return getReal().getLatestThumbnails(user);
  }

  @Override
  public ImmutableMap<LowercaseEmailAddress, FileRole> getPermissions(FileKey fileKey)
      throws AccessException, PersistenceException {

    checker.checkRead(fileKey);

    // TODO(joshuan): Verify this makes sense.
    ImmutableMap<LowercaseEmailAddress, FileRole> permissions = getReal().getPermissions(fileKey);

    // Filter the NONE file roles because the information should not leak. We keep it
    // only because maybe it could be useful at some point. Maybe we don't really need it
    // and might as well filter in the setter.
    ImmutableMap.Builder<LowercaseEmailAddress, FileRole> filteredPermissions =
        ImmutableMap.builder();
    for (Map.Entry<LowercaseEmailAddress, FileRole> entry : permissions.entrySet()) {
      if (entry.getValue() != FileRole.NONE) {
        filteredPermissions.put(entry.getKey(), entry.getValue());
      }
    }

    return filteredPermissions.build();
  }

  @Override
  public void updatePermissions(FileKey fileKey, ImmutableMap<LowercaseEmailAddress, FileRole> diff)
      throws AccessException, PersistenceException, RemovingLastOwnerException {

    checker.checkOwn(fileKey);

    getReal().updatePermissions(fileKey, diff);
  }

  @Override
  public ImmutableMap<LowercaseEmailAddress, FileRole> setPermissions(FileKey fileKey,
      ImmutableMap<LowercaseEmailAddress, FileRole> permissions) throws AccessException,
      PersistenceException, RemovingLastOwnerException {

    checker.checkOwn(fileKey);

    return getReal().setPermissions(fileKey, permissions);
  }

  @Override
  public void deleteFile(FileKey fileKey) throws AccessException, PersistenceException,
      DeletingWithCollaboratorsException {

    checker.checkOwn(fileKey);

    getReal().deleteFile(fileKey);
  }

  @Override
  public void setPublic(FileKey fileKey, boolean isPublic) throws AccessException,
      PersistenceException {

    checker.checkOwn(fileKey);

    if (isPublic) {
      getReal().makePublic(fileKey, user);
    } else {
      getReal().makeNonPublic(fileKey);
    }
  }

  @Override
  public boolean isPublic(FileKey fileKey) throws AccessException, PersistenceException {

    return getReal().isPublic(fileKey);
  }

  @Override
  public CircuitHash getCircuitHash(VersionMetadataKey versionKey) throws AccessException,
      PersistenceException {
    checker.checkRead(versionKey);

    return getReal().getCircuitHash(versionKey);
  }
}
