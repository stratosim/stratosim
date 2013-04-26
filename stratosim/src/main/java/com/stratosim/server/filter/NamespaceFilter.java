package com.stratosim.server.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.appengine.api.NamespaceManager;
import com.stratosim.server.Namespace;

public class NamespaceFilter implements javax.servlet.Filter {
  
  @Override
  public void destroy() {
    // nothing
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    // Make sure set() is only called if the current namespace is not already set.
    if (NamespaceManager.get() == null) {
      Namespace.setCurrent();
    }
    chain.doFilter(req, resp);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // nothing
  }

}
