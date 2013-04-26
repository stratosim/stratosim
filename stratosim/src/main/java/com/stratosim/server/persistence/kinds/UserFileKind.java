package com.stratosim.server.persistence.kinds;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.server.algorithm.InvertibleFunction;
import com.stratosim.server.persistence.schema.MapPropertiesKind;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;

public class UserFileKind extends MapPropertiesKind<UserFileKind, FileKey, FileRole> {

  public UserFileKind() {
    super("userFile", new FileKeyStringFunction(), new FileRoleKind.FileRoleStringFunction());
  }

  private static class FileKeyStringFunction implements InvertibleFunction<FileKey, String> {

    @Override
    public String forward(FileKey fileKey) {
      checkNotNull(fileKey);
      return fileKey.get();
    }

    @Override
    public FileKey reverse(String key) {
      checkNotNull(key);
      return new FileKey(key);
    }
  }

}
