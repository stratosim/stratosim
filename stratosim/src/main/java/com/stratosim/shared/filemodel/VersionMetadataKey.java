package com.stratosim.shared.filemodel;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * Immutable.
 */
public final class VersionMetadataKey extends StratoSimKey {

  private static final long serialVersionUID = 4348270138847941733L;
  
  private static final char SEPARATOR = '/';
  private static final Joiner JOINER = Joiner.on(SEPARATOR);
  private static final Splitter SPLITTER = Splitter.on(SEPARATOR);

  private /*final*/ FileKey fileKey;
  private /*final*/ long timeMillis;  // can't use joda Instant... not GWT compatible
  
  @SuppressWarnings("unused")
  private VersionMetadataKey() {}  // for GWT RPC
  
  public VersionMetadataKey(FileKey fileKey, long timeMillis) {
    super(JOINER.join(fileKey.get(), Long.toString(timeMillis)));
    this.fileKey = fileKey;
    this.timeMillis = timeMillis;
  }
  
  public static VersionMetadataKey parseFrom(String keyString) {
    ImmutableList<String> parts = ImmutableList.copyOf(SPLITTER.split(keyString));
    checkArgument(parts.size() == 2);
    
    return new VersionMetadataKey(new FileKey(parts.get(0)), Long.parseLong(parts.get(1)));
  }
  
  public FileKey getFileKey() {
    return fileKey;
  }
  
  public long getTimeMillis() {
    return timeMillis;
  }
  
  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("fileKey", fileKey)
        .add("timeMillis", timeMillis)
        .toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fileKey == null) ? 0 : fileKey.hashCode());
    result = prime * result + (int) (timeMillis ^ (timeMillis >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    VersionMetadataKey other = (VersionMetadataKey) obj;
    if (fileKey == null) {
      if (other.fileKey != null) {
        return false;
      }
    } else if (!fileKey.equals(other.fileKey)) {
      return false;
    }
    if (timeMillis != other.timeMillis) {
      return false;
    }
    return true;
  }

}
