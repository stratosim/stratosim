package com.stratosim.server.persistence.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.server.UsersHelper;
import com.stratosim.server.persistence.impl.helpers.DatastoreHelper;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class UserAuthorizationChecker {

  private final RealPersistenceLayer layer;
  private final LowercaseEmailAddress user;

  public UserAuthorizationChecker(RealPersistenceLayer layer, DatastoreHelper datastoreHelper,
      LowercaseEmailAddress user) {
    this.layer = layer;
    this.user = user;
  }

  public void checkOwn(FileKey fileKey) throws AccessException, PersistenceException {

    checkNotNull(fileKey);

    if (getIsOwner(fileKey)) {
      return;
    } else {
      throw new AccessException();
    }
  }

  public void checkRead(FileKey fileKey) throws AccessException, PersistenceException {

    checkNotNull(fileKey);

    // Let admins view anything
    if (UsersHelper.isCurrentUserAdmin()) {
      return;
    }

    if (getIsReader(fileKey)) {
      return;
    } else {
      throw new AccessException();
    }
  }

  public void checkRead(VersionMetadataKey versionMetadataKey) throws AccessException,
      PersistenceException {

    // Let admins view anything
    if (UsersHelper.isCurrentUserAdmin()) {
      return;
    }

    FileKey fileKey = checkNotNull(versionMetadataKey).getFileKey();

    if (getIsReader(fileKey)) {
      return;
    } else {
      throw new AccessException();
    }
  }

  public void checkWrite(FileKey fileKey) throws AccessException, PersistenceException {

    checkNotNull(fileKey);

    if (getIsWriter(fileKey)) {
      return;
    } else {
      throw new AccessException();
    }
  }

  public void checkWrite(Circuit circuit) throws AccessException, PersistenceException {

    FileKey fileKey = checkNotNull(circuit).getFileKey();

    if (fileKey == null || getIsWriter(fileKey)) {
      return;
    } else {
      throw new AccessException();
    }
  }

  public boolean getIsOwner(FileKey fileKey) throws PersistenceException {

    FileRole fileRole = getFileRole(checkNotNull(fileKey));

    return fileRole == FileRole.OWNER;
  }

  public boolean getIsWriter(FileKey fileKey) throws PersistenceException {

    FileRole fileRole = getFileRole(checkNotNull(fileKey));

    return fileRole == FileRole.WRITER || fileRole == FileRole.OWNER;
  }

  public boolean getIsReader(FileKey fileKey) throws PersistenceException {

    // Public files are accessible to anyone, even unlogged in users.
    // This allows the embedding widget to function.
    if (layer.isPublic(fileKey)) {
      return true;
    }

    FileRole fileRole = getFileRole(checkNotNull(fileKey));

    return fileRole == FileRole.READER || fileRole == FileRole.WRITER || fileRole == FileRole.OWNER;
  }

  public FileRole getFileRole(FileKey fileKey) throws PersistenceException {
    return layer.getPermission(fileKey, user);
  }

}
