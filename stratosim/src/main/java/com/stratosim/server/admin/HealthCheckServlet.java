package com.stratosim.server.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stratosim.server.serving.AbstractStratoSimServlet;

public class HealthCheckServlet extends AbstractStratoSimServlet {

  private static final long serialVersionUID = 8430762487174980208L;

  @Override
  protected void doHandledGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    resp.setContentType("text/plain");
    resp.getWriter().write("ok");
  }

}
