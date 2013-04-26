package com.stratosim.server.persistence;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.UnsignedInteger;

public interface ObjectifyTestPersistenceLayer {

  <T> ImmutableList<T> getAll(Class<T> entityClass, UnsignedInteger maxNumber);
  
  <T> T getById(Class<T> entityClass, long id);
  
  <T> T getById(Class<T> entityClass, String id);
}
