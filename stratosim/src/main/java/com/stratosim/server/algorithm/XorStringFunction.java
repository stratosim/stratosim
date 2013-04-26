package com.stratosim.server.algorithm;

import java.math.BigInteger;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Preconditions;

public class XorStringFunction implements InvertibleFunction<String, String> {

  private final BigInteger xor;

  public XorStringFunction(BigInteger xor) {
    this.xor = xor;
  }
  
  @Override
  public String forward(String t) {
    Preconditions.checkArgument(t.length() > 0);

    byte[] bytes = t.getBytes();
    BigInteger bi = new BigInteger(bytes);
    bytes = bi.xor(xor).toByteArray();
    String out = Base64.encodeBase64String(bytes);

    return out;
  }

  @Override
  public String reverse(String u) {
    Preconditions.checkArgument(u.length() > 0);

    byte[] bytes = Base64.decodeBase64(u);
    BigInteger bi = new BigInteger(bytes);
    bytes = bi.xor(xor).toByteArray();
    String out = new String(bytes);

    return out;
  }

}
