package com.stratosim.server.serving;

import javax.servlet.http.HttpServletRequest;

import com.stratosim.client.DirectData;
import com.stratosim.server.UsersHelper;
import com.stratosim.shared.DirectClientData;

public class DirectDataImpl implements DirectData {

  private final HttpServletRequest request;

  public DirectDataImpl(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public DirectClientData getData() {
    return new DirectClientData(UsersHelper.getCurrentUser(request),
      UsersHelper.isStratoSimAccount(request));
  }
}
