package com.stratosim.server.persistence;

import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public interface WhitelistPersistenceLayer {

  void putWhitelisted(LowercaseEmailAddress user) throws IllegalArgumentException, PersistenceException;

}
