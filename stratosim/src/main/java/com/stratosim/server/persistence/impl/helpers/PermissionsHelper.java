package com.stratosim.server.persistence.impl.helpers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.stratosim.server.persistence.kinds.FileRoleKind;
import com.stratosim.server.persistence.kinds.UserFileKind;
import com.stratosim.server.persistence.schema.CustomKeyFactory;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.RemovingLastOwnerException;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class PermissionsHelper {

  private final DatastoreHelper datastoreHelper;

  public PermissionsHelper(DatastoreHelper helper) {
    this.datastoreHelper = helper;
  }

  public static void checkAtLeastOneOwner(FileKey fileKey, Collection<FileRole> roles)
      throws RemovingLastOwnerException {
    if (!roles.contains(FileRole.OWNER)) {
      throw new RemovingLastOwnerException(fileKey);
    }
  }

  public static void checkNoNone(Collection<FileRole> roles) {
    checkArgument(!roles.contains(FileRole.NONE));
  }

  public static void applyPermissionDiff(Map<LowercaseEmailAddress, FileRole> fileRole,
      Map<FileKey, FileRole> userFile, LowercaseEmailAddress user, FileKey fileKey, FileRole diff) {
    if (diff == FileRole.NONE) { // erase the permission
      fileRole.remove(user);
      userFile.remove(fileKey);
    } else { // update the permission
      fileRole.put(user, diff);
      userFile.put(fileKey, diff);
    }
  }

  public static ImmutableMap<LowercaseEmailAddress, FileRole> computePermissionsDiff(
      ImmutableMap<LowercaseEmailAddress, FileRole> src, ImmutableMap<LowercaseEmailAddress, FileRole> dst) {
    ImmutableMap.Builder<LowercaseEmailAddress, FileRole> builder = ImmutableMap.builder();

    Set<LowercaseEmailAddress> allUsers = Sets.union(src.keySet(), dst.keySet());
    for (LowercaseEmailAddress user : allUsers) {
      FileRole srcRole = src.get(user);
      FileRole dstRole = dst.get(user);

      if (srcRole == dstRole) {
        // no change
      } else if (dstRole == null) { // erase permission
        builder.put(user, FileRole.NONE);
      } else { // overwrite permission
        builder.put(user, dst.get(user));
      }
    }

    return builder.build();
  }

  public static ImmutableSet<LowercaseEmailAddress> computeNewUsers(
      ImmutableMap<LowercaseEmailAddress, FileRole> src, ImmutableMap<LowercaseEmailAddress, FileRole> dst) {

    Set<LowercaseEmailAddress> newUsers = Sets.difference(dst.keySet(), src.keySet());
    for (LowercaseEmailAddress user : dst.keySet()) {
      if (dst.get(user) == FileRole.NONE) {
        newUsers.remove(user);
      }
    }

    return ImmutableSet.copyOf(newUsers);
  }

  public ImmutableMap<LowercaseEmailAddress, FileRole> getAllFileRoles(FileKey fileKey)
      throws PersistenceException {
    PKey<FileRoleKind> fileRoleKey = CustomKeyFactory.fileRoleKey(fileKey);
    FileRoleKind fileRoleKind = datastoreHelper.uncachedGet(fileRoleKey, new FileRoleKind());
    return ImmutableMap.copyOf(fileRoleKind.properties);
  }

  public ImmutableMap<FileKey, FileRole> getAllUserRoles(LowercaseEmailAddress email) {
    try {
      PKey<UserFileKind> userFileKey = CustomKeyFactory.userFileKey(email);
      UserFileKind userFileKind = datastoreHelper.uncachedGet(userFileKey, new UserFileKind());
      return ImmutableMap.copyOf(userFileKind.properties);

    } catch (PersistenceException ex) {
      // If the user is not found then assume they have no files and return a blank map.
      return ImmutableMap.of();
    }
  }

}
