package com.stratosim.server.serving;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;

public abstract class AbstractStratoSimServlet extends HttpServlet {

  private static final long serialVersionUID = -4326286917410864662L;

  private static final Logger logger = Logger.getLogger(AbstractStratoSimServlet.class
      .getCanonicalName());

  protected void doHandledGet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    throw new UnsupportedOperationException("not implemented.");
  }

  protected void doHandledPost(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    throw new UnsupportedOperationException("not implemented.");
  }

  @Override
  protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      doHandledGet(req, resp);
    } catch (Exception ex) {
      logger.log(Level.SEVERE, getErrorMessage(req), ex);
      serveError(resp, ex);
    }
  }

  @Override
  protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      doHandledPost(req, resp);
    } catch (Exception ex) {
      logger.log(Level.SEVERE, getErrorMessage(req), ex);
      serveError(resp, ex);
    }
  }

  public static void serveError(HttpServletResponse resp, Exception ex) throws IOException {
    if (ex instanceof AccessException) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN);
    } else if (ex instanceof PersistenceException) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    } else if (ex instanceof IllegalArgumentException) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  public static String getErrorMessage(HttpServletRequest req) {
    StringBuilder builder = new StringBuilder();
    builder.append("error handling request: ");
    builder.append("url=" + req.getRequestURL().toString() + ", ");
    builder.append("queryString=" + req.getQueryString() + ", ");
    builder.append("remoteAddr=" + req.getRemoteAddr() + ", ");
    builder.append("remoteUser=" + req.getRemoteUser() + ", ");
    return builder.toString();
  }
}
