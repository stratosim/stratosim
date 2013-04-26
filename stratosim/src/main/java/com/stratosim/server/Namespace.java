package com.stratosim.server;

import com.google.appengine.api.NamespaceManager;
import com.google.common.base.Joiner;

public enum Namespace {
  // INFO: topmost value is considered current (for getCurrent() below)
  v2012_03_18,  // using ParameterValueValidator (diana)
  v2012_02_01,  // using new FileVersionsKind (chimp)
  v2012_01_27;  // using new UserFileKind and FileRoleKind (baboon)
  
  private static final String SERVE_PREFIX = "serve";
  
  private static final Joiner namespaceJoiner = Joiner.on("-");

  public static Namespace getCurrent() {
    return values()[0];
  }
  
  public void set() {
    NamespaceManager.set(namespaceJoiner.join(SERVE_PREFIX, toString()));
  }
  
  public static void setCurrent() {
    getCurrent().set();
  }
}
