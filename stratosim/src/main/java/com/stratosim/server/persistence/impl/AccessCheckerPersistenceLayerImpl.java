package com.stratosim.server.persistence.impl;

import com.stratosim.server.persistence.AccessCheckPersistenceLayer;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class AccessCheckerPersistenceLayerImpl extends RealPersistenceLayerWrapper
    implements
      AccessCheckPersistenceLayer {

  @Override
  public boolean isWhitelisted(LowercaseEmailAddress current) {
    return getReal().isWhitelistedUser(current);
  }

}
