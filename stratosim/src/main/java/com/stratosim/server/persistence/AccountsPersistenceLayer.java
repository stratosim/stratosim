package com.stratosim.server.persistence;

import com.stratosim.shared.PersistenceException;


public interface AccountsPersistenceLayer {

  void putAccount(String usernamePasswordHash) throws IllegalArgumentException,
  PersistenceException;
  
  boolean isValidAccount(String usernamePasswordHash) throws IllegalArgumentException,
  PersistenceException;

}
