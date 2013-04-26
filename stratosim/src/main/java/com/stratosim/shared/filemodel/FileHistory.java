package com.stratosim.shared.filemodel;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;

public class FileHistory implements Serializable {

  private static final long serialVersionUID = -5093135337483521534L;

  // Name of most recent circuit.
  private String name;
  private FileKey fileKey;

  Map<VersionMetadataKey, VersionMetadata> versions = Maps.newHashMap();

  @SuppressWarnings("unused")
  // for GWT RPC
  private FileHistory() {}

  public FileHistory(FileKey fileKey, String name) {
    this.fileKey = fileKey;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public FileKey getFileKey() {
    return fileKey;
  }

  public void add(VersionMetadataKey versionKey, VersionMetadata metadata) {
    versions.put(versionKey, metadata);
  }

  public VersionMetadata getMostRecent() {
    if (versions.isEmpty()) {
      return null;
    }
    return Collections.max(versions.values(), new VersionByDateComparator());
  }

  public VersionMetadata get(VersionMetadataKey versionKey) {
    return versions.get(versionKey);
  }

  public ImmutableSortedMap<Date, VersionMetadataKey> getVersions() {
    SortedMap<Date, VersionMetadataKey> map = Maps.newTreeMap();
    for (VersionMetadata metadata : versions.values()) {
      map.put(metadata.getDate(), metadata.getVersionKey());
    }
    return ImmutableSortedMap.copyOf(map);
  }
}
