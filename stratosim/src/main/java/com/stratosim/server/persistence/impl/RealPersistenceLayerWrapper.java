package com.stratosim.server.persistence.impl;

import com.stratosim.server.persistence.impl.helpers.DatastoreHelper;

class RealPersistenceLayerWrapper {

  private final DatastoreHelper helper;
  private final RealPersistenceLayer realPersistenceLayer;

  protected RealPersistenceLayerWrapper() {
    this.helper = new DatastoreHelper();
    this.realPersistenceLayer = new RealPersistenceLayer(helper);
  }

  protected DatastoreHelper getHelper() {
    return helper;
  }

  protected RealPersistenceLayer getReal() {
    return realPersistenceLayer;
  }

}
