package com.stratosim.server.persistence.objectify.entity;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

@Entity(name = "account")
@Cache(expirationSeconds = 0)  // no expiration
public class Account {

  // Old schema had a field with name "user". We're avoiding using that name here because
  // it causes an objectify error. We're just going to populate userEmail from the key name.
  @Id private String userEmail;
  
  private String salt;
  
  private String hash;
  
  private Account() {}
  
  public LowercaseEmailAddress getUser() {
    return new LowercaseEmailAddress(userEmail);
  }
  
  public String getSalt() {
    return salt;
  }
  
  public String getHash() {
    return hash;
  }
}
