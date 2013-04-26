package com.stratosim.shared.filemodel;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public class LowercaseEmailAddress extends EmailAddress implements Serializable, IsSerializable {
  private static final long serialVersionUID = 8949974488871442907L;

  protected LowercaseEmailAddress() {
    super();
  }

  public LowercaseEmailAddress(String email) {
    super(email.toLowerCase());
  }

}
