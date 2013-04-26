package com.stratosim.shared.filemodel;

import java.io.Serializable;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Immutable email address. Takes a string only and does basic validation.
 */
// Immutable
public class EmailAddress implements Serializable, IsSerializable {
  private static final long serialVersionUID = 140758349999872238L;

  private/*final*/String email;

  // for GWT RPC
  protected EmailAddress() {}
  
  public EmailAddress(String email) {
    if (!isValid(email)) {
      throw new IllegalArgumentException(email);
    }
    this.email = email;
  }
  
  public static boolean isValid(String email) {
    final RegExp EMAIL_PATTERN = RegExp.compile(
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$", "i");
    return EMAIL_PATTERN.test(email);
  }

  public String getEmail() {
    return email;
  }
  
  public String getDomain() {
    return email.split("@")[1];
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    EmailAddress other = (EmailAddress) obj;
    if (email == null) {
      if (other.email != null) return false;
    } else if (!email.equals(other.email)) return false;
    return true;
  }
  
  @Override
  public String toString() {
    return email;
  }
}
