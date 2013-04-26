package com.stratosim.shared.filemodel;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

/**
 * Subclasses should be immutable (final, no mutable methods, etc.).
 */
public abstract class StratoSimKey implements Serializable {

  private static final long serialVersionUID = -6617337918886158124L;

  private String key;

  protected StratoSimKey() {}  // for GWT RPC

  public StratoSimKey(String key) {
    this.key = checkNotNull(key);
  }

  public String get() {
    return key;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj instanceof StratoSimKey) {
      StratoSimKey other = (StratoSimKey) obj;
      return key.equals(other.key);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public String toString() {
    return "key: " + key;
  }

}
