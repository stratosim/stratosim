package com.stratosim.server.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stratosim.server.UsersHelper;

public class UserAccessFilter implements javax.servlet.Filter {

  private static final Logger logger = Logger.getLogger(UserAccessFilter.class
      .getCanonicalName());

  @Override
  public void destroy() {
    // nothing
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) req;
    if (UsersHelper.isUser(httpRequest)) {
      logger.log(Level.INFO, "User: " + UsersHelper.getCurrentUser(httpRequest));
      chain.doFilter(req, resp);
    } else {
      HttpServletResponse httpResponse = (HttpServletResponse) resp;
      httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // nothing
  }

}
