package com.stratosim.server;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.stratosim.server.login.LoginCookieHelper;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

/**
 * Only for use in com.stratosim.filter.serving, com.stratosim.server.filter.UserAccessFilter
 * com.stratosim.server.secure, com.stratosim.server.admin. Basically for anything
 * directly receiving a request. Do not use in persistence layer stuff. Instead, grab the user and
 * pass that into the persistence layer. This limits changes to the auth system to this singleton.
 * This should not be responsible for admin authentication. That is enforced by directly querying
 * the user service from web.xml (tier 1) and the persistence layer (tier 2).
 *
 * This is a singleton and not a static class for legacy reasons. It is unclear now whether it is
 * worth maintaining it this way.
 *
 */
public class UsersHelper {

  private UsersHelper() {
    throw new UnsupportedOperationException("not instantiable");
  }

  public static @Nullable
  LowercaseEmailAddress getCurrentUser(HttpServletRequest request) {
    if (CookieHelper.getCookieValue(request, "SUID") != null
        && LoginCookieHelper.getUserFromCookie(CookieHelper.getCookieValue(request, "SUID")) != null) {
      return new LowercaseEmailAddress(LoginCookieHelper.getUserFromCookie(CookieHelper.getCookieValue(
          request, "SUID")));
    }

    return null;
  }

  public static boolean isStratoSimAccount(HttpServletRequest request) {
    // Assumes that the user is valid.
    return LoginCookieHelper.getIsStratoSimAccountFromCookie(CookieHelper.getCookieValue(request, "SUID"));
  }

  public static boolean isUser(HttpServletRequest request) {
    if (getCurrentUser(request) != null) {
      return true;
    }

    return false;
  }

  public static boolean isCurrentUserAdmin() {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn() && userService.isUserAdmin()) {
      return true;
    } else {
      return false;
    }
  }
}
