package com.stratosim.server.persistence.impl;

import com.stratosim.server.persistence.WhitelistPersistenceLayer;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class WhitelistPersistenceLayerImpl extends RealPersistenceLayerWrapper
    implements
      WhitelistPersistenceLayer {

  public WhitelistPersistenceLayerImpl() {

  }

  @Override
  public void putWhitelisted(LowercaseEmailAddress user) throws IllegalArgumentException,
      PersistenceException {
    getReal().putWhitelistedUser(user);
  }

}
