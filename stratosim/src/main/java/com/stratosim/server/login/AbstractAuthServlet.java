package com.stratosim.server.login;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.utils.SystemProperty;
import com.stratosim.server.CookieHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.serving.AbstractStratoSimServlet;
import com.stratosim.shared.AppPaths;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public abstract class AbstractAuthServlet extends AbstractStratoSimServlet {
  private static final long serialVersionUID = -5746678995368093751L;

  private static final Logger logger = Logger.getLogger(AbstractAuthServlet.class
      .getCanonicalName());

  private void handleRequest(HttpServletRequest request, HttpServletResponse resp)
      throws IOException {
    String verifiedEmail = getEmail(request, resp);
    boolean isStratoSimAccount = isStratoSimAccount();

    if (verifiedEmail == null) {
      logger.log(Level.WARNING, "Unable to get email for user.");
      // TODO(tpondich): Handle this better.
      resp.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    LowercaseEmailAddress user = new LowercaseEmailAddress(verifiedEmail);

    // Check the whitelist in production mode.
    if (!PersistenceLayerFactory.createAccessCheckLayer().isWhitelisted(user)
        && SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
      logger.log(Level.WARNING, "Unwhitelisted user: " + user);
      resp.sendRedirect("/#signup");
      return;
    }

    CookieHelper.setCookieValue(resp, "SUID",
        LoginCookieHelper.getCookieString(verifiedEmail, isStratoSimAccount), true, true);
    resp.sendRedirect(maybeAddCuid(AppPaths.APP_PATH + "/", request.getParameter("cuid")));
    return;

  }

  @Override
  protected final void doHandledGet(HttpServletRequest request, HttpServletResponse resp)
      throws IOException {
    handleRequest(request, resp);
  }

  @Override
  protected final void doHandledPost(HttpServletRequest request, HttpServletResponse resp)
      throws IOException {
    handleRequest(request, resp);
  }

  // TODO(tpondich): Look for a URL building library.
  private static String maybeAddCuid(String url, @Nullable String cuid) {
    if (cuid != null) {
      if (url.contains("?")) {
        return url + "&cuid=" + cuid;
      } else {
        return url + "?cuid=" + cuid;
      }
    } else {
      return url;
    }
  }

  protected abstract String getEmail(HttpServletRequest request, HttpServletResponse response)
      throws IOException;

  protected boolean isStratoSimAccount() {
    return false;
  }

  protected @Nullable
  String googleAPIsAccessToken() {
    return null;
  }
}
