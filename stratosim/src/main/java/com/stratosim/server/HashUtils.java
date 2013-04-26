package com.stratosim.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashUtils {

  private HashUtils() {
    throw new UnsupportedOperationException("uninstantiable");
  }

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public static SecureRandom secureRandom() {
    return SECURE_RANDOM;
  }
  
  public static String sha256String(String string) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("SHA-256");
      md.update(string.getBytes());
      return String.format("%064x", new BigInteger(1, md.digest()));
    } catch (NoSuchAlgorithmException e) {
      // Impossible in practice.
      throw new IllegalStateException("Hashing Algorithm Not Found: SHA-256");
    }
  }
}
