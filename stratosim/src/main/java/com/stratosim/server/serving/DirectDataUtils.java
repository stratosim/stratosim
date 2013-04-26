package com.stratosim.server.serving;

import java.lang.reflect.Method;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.stratosim.client.DirectData;
import com.stratosim.shared.DirectClientData;

public class DirectDataUtils {

  private DirectDataUtils() {
    throw new UnsupportedOperationException("uninstantiable");
  }

  public static String getDirectDataString(DirectClientData data) {
    Method method;
    try {
      method = DirectData.class.getMethod("getData");
      String encoded = RPC.encodeResponseForSuccess(method, data);
      return String.format("%s='%s';\n", DirectClientData.JS_NAME, encoded); 

    } catch (NoSuchMethodException ex) {
      throw new IllegalStateException(ex);
    } catch (SerializationException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
