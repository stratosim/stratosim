package com.stratosim.server.login;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stratosim.server.CookieHelper;
import com.stratosim.server.serving.AbstractStratoSimServlet;

public class DeAuthServlet extends AbstractStratoSimServlet {
  private static final long serialVersionUID = -5746678995368093751L;
  
  @Override
  protected void doHandledGet(HttpServletRequest request, HttpServletResponse resp)
      throws IOException {
    CookieHelper.clearCookie(resp, "SUID", true, true);
    resp.sendRedirect("/");
  }

}
