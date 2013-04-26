package com.stratosim.server.persistence;

import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public interface AccessCheckPersistenceLayer {

  boolean isWhitelisted(LowercaseEmailAddress current);

}
