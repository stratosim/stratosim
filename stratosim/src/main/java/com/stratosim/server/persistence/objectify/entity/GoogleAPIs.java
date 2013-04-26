package com.stratosim.server.persistence.objectify.entity;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

@Entity(name = "googleAPIs")
@Cache(expirationSeconds = 0)  // no expiration
public class GoogleAPIs {

  // Old schema had a field with name "email". We're avoiding using that name here because
  // it causes an objectify error. We're just going to populate userEmail from the key name.
  @Id private String userEmail;

  private String refreshToken;

  private String accessToken;
  
  private GoogleAPIs() {}

  public LowercaseEmailAddress getEmail() {
    return new LowercaseEmailAddress(userEmail);
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }
}
