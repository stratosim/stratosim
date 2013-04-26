package com.stratosim.shared.filemodel;

/**
 * Immutable.
 */
public final class FileKey extends StratoSimKey {

  private static final long serialVersionUID = 5133539072376016529L;

  @SuppressWarnings("unused")
  // for GWT RPC
  private FileKey() {}

  public FileKey(String key) {
    super(key);
  }

}
