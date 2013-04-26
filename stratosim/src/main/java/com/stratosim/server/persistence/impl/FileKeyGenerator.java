package com.stratosim.server.persistence.impl;

import org.apache.commons.codec.binary.Base64;

import com.stratosim.server.HashUtils;
import com.stratosim.shared.filemodel.FileKey;

public class FileKeyGenerator {
  
  private static final int NUM_BYTES = 8;

  private FileKeyGenerator() {
    throw new UnsupportedOperationException("uninstantiable");
  }

  public static FileKey newFileKey() {
    byte[] bytes = new byte[NUM_BYTES];
    HashUtils.secureRandom().nextBytes(bytes);
    return new FileKey(Base64.encodeBase64URLSafeString(bytes));
  }

}
