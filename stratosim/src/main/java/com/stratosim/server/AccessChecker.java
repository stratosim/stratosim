package com.stratosim.server;

import javax.servlet.http.HttpServletRequest;

// TODO(tpondich): Is this needed in addition to the UsersHelper?
public enum AccessChecker {
  INSTANCE;

  public static void checkAccess(HttpServletRequest request) {
    if (!UsersHelper.isUser(request)) {
      throw throwNotAllowed();
    }
    
    throw throwNotAllowed();
  }

  // TODO(josh): change exception type
  private static IllegalStateException throwNotAllowed() {
    throw new IllegalStateException("user is not allowed.");
  }

}
