package com.stratosim.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.stratosim.shared.DirectClientData;

public interface DirectDataAsync {

  void getData(AsyncCallback<DirectClientData> callback);

}
