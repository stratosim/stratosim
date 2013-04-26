package com.stratosim.server.persistence;

import org.joda.time.Instant;

import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.RemovingLastOwnerException;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public interface AdminPersistenceLayer {

  void setPermission(LowercaseEmailAddress user, FileKey fileKey, FileRole role)
      throws RemovingLastOwnerException;

  ImmutableMap<LowercaseEmailAddress, FileRole> getFilePermissions(FileKey fileKey) throws PersistenceException;

  ImmutableMap<FileKey, FileRole> getUserPermissions(LowercaseEmailAddress user);

  void makePublic(FileKey fileKey);

  ImmutableSortedMap<Instant, VersionMetadataPb> getVersionProtos(FileKey fileKey);

  ImmutableList<LowercaseEmailAddress> listAllUsers();

}
