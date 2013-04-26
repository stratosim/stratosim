package com.stratosim.server.login;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.annotation.Nullable;

import com.stratosim.server.AppInfo;
import com.stratosim.server.HashUtils;

public enum LoginCookieHelper {
  INSTANCE;

  public static String getCookieString(String email, boolean isStratoSimAccount) {
    try {
      String signedCookie =
          URLEncoder.encode(AppInfo.COOKIE_ENCRYPT_FUNCTION.forward(HashUtils
              .sha256String(AppInfo.COOKIE_TOKEN_SALT + email + isStratoSimAccount)
              + ";"
              + email
              + ";" + isStratoSimAccount), "UTF-8");
      return signedCookie;
    } catch (UnsupportedEncodingException e) {
      // Impossible in practice.
      throw new IllegalStateException();
    }
  }

  private static @Nullable
  String getPartFromCookie(String signedCookie, int part) {
    try {
      String[] cookieParts =
          AppInfo.COOKIE_ENCRYPT_FUNCTION.reverse(URLDecoder.decode(signedCookie, "UTF-8")).split(
              ";");
      checkState(cookieParts.length == 3);
      checkArgument(part < cookieParts.length);
      String tokenHash = checkNotNull(cookieParts[0]);
      String email = checkNotNull(cookieParts[1]);
      boolean isStratoSimAccount = Boolean.parseBoolean(cookieParts[2]);
      if (tokenHash.equals(HashUtils.sha256String(AppInfo.COOKIE_TOKEN_SALT + email
          + isStratoSimAccount))) {
        return checkNotNull(cookieParts[part]);
      } else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the user's email or null if the cookie is invalid.
   * 
   * @param signedCookie
   * @return
   */
  public static @Nullable
  String getUserFromCookie(String signedCookie) {
    return getPartFromCookie(signedCookie, 1);
  }

  /**
   * Returns whether the user is using a StratoSim Account. Cookie must be valid to call this, or
   * exceptions will result.
   * 
   * @param signedCookie
   * @return
   */
  public static boolean getIsStratoSimAccountFromCookie(String signedCookie) {
    return Boolean.parseBoolean(getPartFromCookie(signedCookie, 2));
  }
}
