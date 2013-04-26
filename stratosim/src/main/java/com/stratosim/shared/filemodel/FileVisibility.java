package com.stratosim.shared.filemodel;

import java.io.Serializable;

import com.google.common.collect.ImmutableMap;

/**
 * Complete visibility specification for a file. This includes the fine-grained permissions
 * (per-user) as well as whether it's globally public.
 */
public class FileVisibility implements Serializable {
  private static final long serialVersionUID = 3550099831140717227L;

  private /*final*/ ImmutableMap<EmailAddress, FileRole> permissions;
  
  private /*final*/ boolean isPublic;
  
  @SuppressWarnings("unused")  // for GWT RPC
  private FileVisibility() {}
  
  public FileVisibility(ImmutableMap<EmailAddress, FileRole> permissions, boolean isPublic) {
    this.permissions = permissions;
    this.isPublic = isPublic;
  }
  
  public ImmutableMap<EmailAddress, FileRole> getPermissions() {
    return permissions;
  }
  
  public boolean isPublic() {
    return isPublic;
  }
  
  @Override
  public String toString() {
    return "FileVisibility [permissions=" + permissions + ", isPublic=" + isPublic + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (isPublic ? 1231 : 1237);
    result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    FileVisibility other = (FileVisibility) obj;
    if (isPublic != other.isPublic) return false;
    if (permissions == null) {
      if (other.permissions != null) return false;
    } else if (!permissions.equals(other.permissions)) return false;
    return true;
  }
}
