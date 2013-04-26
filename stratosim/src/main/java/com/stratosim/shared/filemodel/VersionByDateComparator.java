package com.stratosim.shared.filemodel;

import java.util.Comparator;

/**
 * Compares versions by date. That is, (more recent version) > (older version)
 * 
 */
public class VersionByDateComparator implements Comparator<VersionMetadata> {

  @Override
  public int compare(VersionMetadata v1, VersionMetadata v2) {
    return v1.getDate().compareTo(v2.getDate());
  }

}
