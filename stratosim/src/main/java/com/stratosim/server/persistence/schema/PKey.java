package com.stratosim.server.persistence.schema;

import java.io.Serializable;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public final class PKey<K extends Kind<K>> implements Serializable {

  private static final long serialVersionUID = 7510929409826922025L;

  private final String key;

  public PKey(String key) {
    this.key = key;
  }

  public String getAsString() {
    // Note that the format of the key string is used in various places for validation.
    // EC2 services currently expect base64 encoded strings (must match: ^([0-9a-zA-Z]|_|\$|=)+$)
    return key;
  }
  
  public Key dsKey() {
    return KeyFactory.stringToKey(key);
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
    
    PKey<?> other = (PKey<?>) obj;
    return key.equals(other.key);
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
