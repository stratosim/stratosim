package com.stratosim.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.stratosim.shared.DirectClientData;

public interface DirectData extends RemoteService {

  DirectClientData getData();
  
}
