package com.stratosim.server.persistence.impl;

import com.stratosim.server.persistence.AccountsPersistenceLayer;
import com.stratosim.server.persistence.impl.helpers.AccountsHelper;
import com.stratosim.server.persistence.impl.helpers.DatastoreHelper;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class AccountsPersistenceLayerImpl
    implements AccountsPersistenceLayer {

  private final LowercaseEmailAddress user;
  private final AccountsHelper helper;

  public AccountsPersistenceLayerImpl(LowercaseEmailAddress user) {
    this.user = user;
    this.helper = new AccountsHelper(new DatastoreHelper());
  }

  @Override 
  public void putAccount(String usernamePasswordHash) throws IllegalArgumentException, PersistenceException {
    helper.putAccount(user, usernamePasswordHash);
  }

  @Override 
  public boolean isValidAccount(String usernamePasswordHash) throws IllegalArgumentException, PersistenceException {
    return helper.isValidAccount(user, usernamePasswordHash);
  }
}
