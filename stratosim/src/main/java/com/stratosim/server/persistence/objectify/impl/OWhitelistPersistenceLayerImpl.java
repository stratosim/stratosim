package com.stratosim.server.persistence.objectify.impl;

import static com.stratosim.server.persistence.objectify.OfyService.stratoSimOfy;

import org.joda.time.Instant;

import com.stratosim.server.persistence.WhitelistPersistenceLayer;
import com.stratosim.server.persistence.objectify.entity.WhitelistedUser;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class OWhitelistPersistenceLayerImpl implements WhitelistPersistenceLayer {

  @Override
  public void putWhitelisted(LowercaseEmailAddress user) {
    WhitelistedUser entity = new WhitelistedUser(user, Instant.now());
    stratoSimOfy().save().entity(entity).now();
  }
}
