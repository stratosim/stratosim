package com.stratosim.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CookieHelper {

  private CookieHelper() {
    throw new UnsupportedOperationException("not instantiable");
  }
  
  public final static String getCookieValue(HttpServletRequest request, String cookieName) {
    Cookie[] list = request.getCookies();
    if (list != null) {
      for (int i = 0; i < list.length; i++) {
        try {
          if (list[i].getName().equals(URLEncoder.encode(cookieName, "UTF-8"))) {
            String cookieValue = list[i].getValue();
            return URLDecoder.decode(cookieValue, "UTF-8");
          }
        } catch (UnsupportedEncodingException e) {
          // Impossible in practice.
          throw new IllegalStateException();
        }
      }
    }
    return null;
  }

  // TODO(tpondich): Support multiple cookies.
  public final static void setCookieValue(HttpServletResponse response, String cookieName,
      String cookieValue, boolean httpOnly, boolean secure) {
    String cookieString = encodeCookieValue(cookieName, cookieValue, httpOnly, secure, -1);
    response.addHeader("Set-Cookie", cookieString);
  }
  
  public final static void clearCookie(HttpServletResponse response, String cookieName, boolean httpOnly, boolean secure) {
    String cookieString = encodeCookieValue(cookieName, "", httpOnly, secure, 0);
    response.addHeader("Set-Cookie", cookieString);
  }
  
  public final static String encodeCookieValue(String cookieName,
      String cookieValue, boolean httpOnly, boolean secure, int maxAge) {
    // In hosted mode, we can't use SSL so make all cookies not secure.
    if (AppInfo.isDevelopment()) {
      secure = false;
    }
    
    try {
      String cookieString = URLEncoder.encode(cookieName, "UTF-8") + "=" + URLEncoder.encode(cookieValue, "UTF-8")
          + "; Path=/;" + (httpOnly ? " HttpOnly; " : "") + (secure ? " Secure; " : "") + (maxAge >= 0 ? "Max-Age=" + maxAge : "");
      return cookieString;
    } catch (UnsupportedEncodingException e) {
      // Impossible in practice.
      throw new IllegalStateException();
    }
  }
}
