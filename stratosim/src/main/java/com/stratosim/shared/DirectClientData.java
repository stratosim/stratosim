package com.stratosim.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.stratosim.shared.filemodel.EmailAddress;

/**
 * Stores data that the server sends down to the client (to avoid extra RPCs).
 * 
 * Only add IsSerializable fields (explicit RPC encoding didn't seem to like
 * Serializable objects -- not sure why).
 */
public class DirectClientData implements IsSerializable {
  
  public static final String JS_NAME = "direct_data";
  
  private /*final*/ EmailAddress userEmail;
  
  private /*final*/ boolean isStratoSimAccount;
  
  @SuppressWarnings("unused")  // for GWT RPC
  private DirectClientData() {}
  
  public DirectClientData(EmailAddress userEmail, boolean isStratoSimAccount) {
    this.userEmail = userEmail;
    this.isStratoSimAccount = isStratoSimAccount;
  }

  public EmailAddress getUserEmail() {
    return userEmail;
  }

  public boolean isStratoSimAccount() {
    return isStratoSimAccount;
  }
}
