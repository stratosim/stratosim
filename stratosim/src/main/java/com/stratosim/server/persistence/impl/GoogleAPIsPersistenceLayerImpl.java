package com.stratosim.server.persistence.impl;

import com.stratosim.server.persistence.GoogleAPIsPersistenceLayer;
import com.stratosim.server.persistence.impl.helpers.DatastoreHelper;
import com.stratosim.server.persistence.impl.helpers.GoogleAPIsHelper;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class GoogleAPIsPersistenceLayerImpl
    implements GoogleAPIsPersistenceLayer {

  private final LowercaseEmailAddress user;
  private final GoogleAPIsHelper helper;

  public GoogleAPIsPersistenceLayerImpl(LowercaseEmailAddress user) {
    this.user = user;
    this.helper = new GoogleAPIsHelper(new DatastoreHelper());
  }

  @Override 
  public void putRefreshToken(String refreshToken) {
    helper.putRefreshToken(user, refreshToken);
  }

  @Override 
  public String getRefreshToken() throws PersistenceException {
    return helper.getRefreshToken(user);
  }
  
  @Override 
  public void putAccessToken(String accessToken) {
    helper.putAccessToken(user, accessToken);
  }

  @Override 
  public String getAccessToken() throws PersistenceException {
    return helper.getAccessToken(user);
  }
}
