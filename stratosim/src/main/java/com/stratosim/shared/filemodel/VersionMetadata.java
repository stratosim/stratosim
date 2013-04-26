package com.stratosim.shared.filemodel;

import java.io.Serializable;
import java.util.Date;

/**
 * Immutable
 */
public class VersionMetadata implements Serializable {

  private static final long serialVersionUID = -4301725428419354596L;

  private /*final*/ VersionMetadataKey versionKey;
  private /*final*/ String name;

  @SuppressWarnings("unused")
  // GWT Serialization
  private VersionMetadata() {}

  public VersionMetadata(String name, VersionMetadataKey versionKey) {
    this.name = name;
    this.versionKey = versionKey;
  }

  public FileKey getFileKey() {
    return versionKey.getFileKey();
  }

  public VersionMetadataKey getVersionKey() {
    return versionKey;
  }

  public String getName() {
    return name;
  }

  public Date getDate() {
    return new Date(versionKey.getTimeMillis());
  }

}
