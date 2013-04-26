package com.stratosim.server.persistence.kinds;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stratosim.server.algorithm.InvertibleFunction;
import com.stratosim.server.persistence.schema.MapPropertiesKind;
import com.stratosim.server.persistence.schema.Type;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class FileRoleKind extends MapPropertiesKind<FileRoleKind, LowercaseEmailAddress, FileRole> {

  public FileRoleKind() {
    super("fileRole", new LowercaseEmailStringFunction(), new FileRoleStringFunction());
  }

  private static class LowercaseEmailStringFunction
      implements InvertibleFunction<LowercaseEmailAddress, String> {

    @Override
    public String forward(LowercaseEmailAddress email) {
      checkNotNull(email);
      return email.getEmail();
    }

    @Override
    public LowercaseEmailAddress reverse(String email) {
      checkNotNull(email);
      return new LowercaseEmailAddress(email);
    }
  }

  static class FileRoleStringFunction implements InvertibleFunction<FileRole, Object> {

    @Override
    public String forward(FileRole role) {
      checkNotNull(role);
      return role.toString();
    }

    @Override
    public FileRole reverse(Object obj) {
      checkNotNull(obj);
      String s = Type.tryCast(obj);
      return FileRole.valueOf(s);
    }

  }

}
