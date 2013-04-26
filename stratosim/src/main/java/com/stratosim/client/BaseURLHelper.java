package com.stratosim.client;

import static com.google.gwt.core.client.GWT.getHostPageBaseURL;

/**
 * Basic URL parsing. Can't use java.net.URL because it won't GWT compile.
 * 
 * @author tarun
 *
 */
public class BaseURLHelper {
  
  private BaseURLHelper() {
    throw new UnsupportedOperationException("not instantiable");
  }
  
  public final static String getDomain() {
    String url = getHostPageBaseURL();
    String urlWithoutProtocol = url.substring(url.indexOf("://") + 3);
    String domain;
    if (urlWithoutProtocol.indexOf(":") != -1) {
      domain = urlWithoutProtocol.substring(0, urlWithoutProtocol.indexOf(":"));
    } else {
      domain = urlWithoutProtocol.substring(0, urlWithoutProtocol.indexOf("/"));
    }
    return domain;
  }
  
  public final static String getServingBase() {
    String url = getHostPageBaseURL();
    String protocol = url.substring(0, url.indexOf("://") + 3);
    String urlWithoutProtocol = url.substring(url.indexOf("://") + 3);
    String domainAndPort = urlWithoutProtocol.substring(0, urlWithoutProtocol.indexOf("/"));

    return protocol + domainAndPort;
  }
}
