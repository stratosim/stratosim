package com.stratosim.server.persistence;

import com.stratosim.shared.PersistenceException;


public interface GoogleAPIsPersistenceLayer {

  void putRefreshToken(String refreshToken);
  
  String getRefreshToken() throws PersistenceException;

  void putAccessToken(String accessToken);

  String getAccessToken() throws PersistenceException;

}
