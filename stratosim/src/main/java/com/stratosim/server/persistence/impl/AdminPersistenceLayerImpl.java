package com.stratosim.server.persistence.impl;

import org.joda.time.Instant;

import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.stratosim.server.persistence.AdminPersistenceLayer;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.RemovingLastOwnerException;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class AdminPersistenceLayerImpl extends RealPersistenceLayerWrapper
    implements AdminPersistenceLayer {

  private final LowercaseEmailAddress user;

  public AdminPersistenceLayerImpl(LowercaseEmailAddress user) {
    this.user = user;
  }

  @Override
  public void setPermission(LowercaseEmailAddress user, FileKey fileKey, FileRole role)
      throws RemovingLastOwnerException {
    getReal().updatePermissions(fileKey, ImmutableMap.of(user, role));
  }

  @Override
  public void makePublic(FileKey fileKey) {
    getReal().makePublic(fileKey, user);
  }

  @Override
  public ImmutableSortedMap<Instant, VersionMetadataPb> getVersionProtos(FileKey fileKey) {
    return getReal().getVersionProtos(fileKey);
  }

  @Override
  public ImmutableMap<LowercaseEmailAddress, FileRole> getFilePermissions(FileKey fileKey)
      throws PersistenceException {
    return getReal().getPermissions(fileKey);
  }

  @Override
  public ImmutableMap<FileKey, FileRole> getUserPermissions(LowercaseEmailAddress user) {
    return getReal().getPermissions(user);
  }

  @Override
  public ImmutableList<LowercaseEmailAddress> listAllUsers() {
    return getReal().listAllUsers();
  }

}
