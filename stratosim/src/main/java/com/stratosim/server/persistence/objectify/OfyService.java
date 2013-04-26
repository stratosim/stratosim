package com.stratosim.server.persistence.objectify;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.stratosim.server.persistence.objectify.entity.Account;
import com.stratosim.server.persistence.objectify.entity.CircuitCsv;
import com.stratosim.server.persistence.objectify.entity.CircuitData;
import com.stratosim.server.persistence.objectify.entity.CircuitPdf;
import com.stratosim.server.persistence.objectify.entity.CircuitPng;
import com.stratosim.server.persistence.objectify.entity.CircuitPs;
import com.stratosim.server.persistence.objectify.entity.CircuitSimulationPdf;
import com.stratosim.server.persistence.objectify.entity.CircuitSimulationPng;
import com.stratosim.server.persistence.objectify.entity.CircuitSimulationPs;
import com.stratosim.server.persistence.objectify.entity.CircuitSpice;
import com.stratosim.server.persistence.objectify.entity.CircuitSvg;
import com.stratosim.server.persistence.objectify.entity.CircuitThumbnail;
import com.stratosim.server.persistence.objectify.entity.DeletedFile;
import com.stratosim.server.persistence.objectify.entity.GoogleAPIs;
import com.stratosim.server.persistence.objectify.entity.PublicFile;
import com.stratosim.server.persistence.objectify.entity.WhitelistedUser;

public class OfyService {
  
  // TODO(josh): add a test to verify entity package and this set are equal
  @SuppressWarnings("unchecked")  // generic array creation
  @VisibleForTesting
  public static ImmutableSet<Class<?>> ENTITY_CLASSES = ImmutableSet.of(
      Account.class,
      CircuitCsv.class,
      CircuitData.class,
      CircuitPdf.class,
      CircuitPng.class,
      CircuitPs.class,
      CircuitSimulationPdf.class,
      CircuitSimulationPng.class,
      CircuitSimulationPs.class,
      CircuitSpice.class,
      CircuitSvg.class,
      CircuitThumbnail.class,
      DeletedFile.class,
      GoogleAPIs.class,
      PublicFile.class,
      WhitelistedUser.class);
  
  static {
    for (Class<?> entityClass : ENTITY_CLASSES) {
      factory().register(entityClass);
    }
  }

  public static Objectify stratoSimOfy() {
    return ObjectifyService.ofy();
  }
  
  private static ObjectifyFactory factory() {
    return ObjectifyService.factory();
  }
}
