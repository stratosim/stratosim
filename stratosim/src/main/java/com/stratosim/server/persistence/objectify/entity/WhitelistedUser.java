package com.stratosim.server.persistence.objectify.entity;

import org.joda.time.Instant;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

@Entity(name = "whitelistedUser")
@Cache(expirationSeconds = 0)  // no expiration
public class WhitelistedUser {

  // Old schema had a field with name "user". We're avoiding using that name here because
  // it causes an objectify error. We're just going to populate userEmail from the key name.
  @Id private String userEmail;
  
  private long accessStartMillis;
  
  private WhitelistedUser() {}
  
  public WhitelistedUser(LowercaseEmailAddress userEmail, Instant accessStart) {
    this.userEmail = userEmail.getEmail();
    this.accessStartMillis = accessStart.getMillis();
  }
  
  public LowercaseEmailAddress getEmail() {
    return new LowercaseEmailAddress(userEmail);
  }
  
  public Instant getAccessStart() {
    return new Instant(accessStartMillis);
  }
}
