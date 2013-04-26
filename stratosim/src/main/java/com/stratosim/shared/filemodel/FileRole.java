package com.stratosim.shared.filemodel;

import java.io.Serializable;

/**
 * The toString() values of these (the instance names) are persisted in the datastore and
 * should NOT be changed.
 */
public enum FileRole implements Serializable {
  NONE,
  OWNER,
  WRITER,
  READER
}
