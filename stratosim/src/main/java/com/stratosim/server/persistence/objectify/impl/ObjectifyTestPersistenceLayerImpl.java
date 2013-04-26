package com.stratosim.server.persistence.objectify.impl;

import static com.stratosim.server.persistence.objectify.OfyService.stratoSimOfy;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.UnsignedInteger;
import com.stratosim.server.persistence.ObjectifyTestPersistenceLayer;

public class ObjectifyTestPersistenceLayerImpl implements ObjectifyTestPersistenceLayer {

  @Override
  public <T> ImmutableList<T> getAll(Class<T> entityClass, UnsignedInteger limit) {
    return ImmutableList.copyOf(stratoSimOfy().load().type(entityClass).limit(limit.intValue()));
  }

  @Override
  public <T> T getById(Class<T> entityClass, long id) {
    return stratoSimOfy().load().type(entityClass).id(id).get();
  }

  @Override
  public <T> T getById(Class<T> entityClass, String id) {
    return stratoSimOfy().load().type(entityClass).id(id).get();
  }
}
