package com.stratosim.server.persistence.impl.helpers;

import com.stratosim.server.persistence.kinds.GoogleAPIsKind;
import com.stratosim.server.persistence.schema.CustomKeyFactory;
import com.stratosim.server.persistence.schema.PKey;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class GoogleAPIsHelper {
  
  private final DatastoreHelper datastoreHelper;
  
  public GoogleAPIsHelper(DatastoreHelper helper) {
    this.datastoreHelper = helper;
  }

  // ---------------------------------------------------------------------------------------------
  // Google API Authorization Tokens
  // ---------------------------------------------------------------------------------------------

  public void putRefreshToken(LowercaseEmailAddress user, String refreshToken) {
    PKey<GoogleAPIsKind> key = CustomKeyFactory.googleAPIsKey(user);

    GoogleAPIsKind kind = new GoogleAPIsKind();
    kind.user.set(user);
    kind.refreshToken.set(refreshToken);
    // The old access token will be wiped out. This is what we want
    // since it's invalid when we set a new refresh token.
    kind.accessToken.set("");
    
    datastoreHelper.overwriteOrPut(key, kind);
  }
  
  
  public String getRefreshToken(LowercaseEmailAddress user) throws PersistenceException {
    PKey<GoogleAPIsKind> key = CustomKeyFactory.googleAPIsKey(user);

    GoogleAPIsKind kind = datastoreHelper.get(key, new GoogleAPIsKind());

    return kind.refreshToken.get();
  }
  
  public void putAccessToken(LowercaseEmailAddress user, String accessToken) {
    PKey<GoogleAPIsKind> key = CustomKeyFactory.googleAPIsKey(user);

    GoogleAPIsKind kind;
    try {
      kind = datastoreHelper.get(key, new GoogleAPIsKind());
    } catch (PersistenceException e) {
      kind = new GoogleAPIsKind();
    }

    kind.user.set(user);
    kind.accessToken.set(accessToken);

    datastoreHelper.overwriteOrPut(key, kind);
  }

  public String getAccessToken(LowercaseEmailAddress user) throws PersistenceException {
    PKey<GoogleAPIsKind> key = CustomKeyFactory.googleAPIsKey(user);

    GoogleAPIsKind kind = datastoreHelper.get(key, new GoogleAPIsKind());

    return kind.accessToken.get();
  }
}
