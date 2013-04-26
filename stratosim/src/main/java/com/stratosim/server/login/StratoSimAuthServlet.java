package com.stratosim.server.login;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stratosim.server.AppInfo;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.AppPaths;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class StratoSimAuthServlet extends AbstractAuthServlet {
  private static final long serialVersionUID = -5746678995368093751L;

  @Override
  protected String getEmail(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String email = checkNotNull(request.getParameter("email"));
    String hash = checkNotNull(request.getParameter("hash"));

    try {
      // Check if the username and password is valid. In development mode, let any password and user
      // through.
      if (AppInfo.isDevelopment()
          || PersistenceLayerFactory.createAccountsLayer(new LowercaseEmailAddress(email))
              .isValidAccount(hash)) {
        return email;
      } else {
        response.sendRedirect(AppPaths.APP_PATH + "/?stratosim_invalid");
      }

    } catch (PersistenceException e) {
      // Username is invalid.
      response.sendRedirect(AppPaths.APP_PATH + "/?stratosim_invalid");

    }

    return null;
  }

  protected boolean isStratoSimAccount() {
    return true;
  }
}
