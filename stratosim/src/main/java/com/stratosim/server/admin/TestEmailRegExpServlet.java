package com.stratosim.server.admin;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stratosim.shared.filemodel.EmailAddress;

public class TestEmailRegExpServlet extends HttpServlet {

  private static final long serialVersionUID = 8221845039514222732L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    resp.setContentType("text/html");
    resp.getWriter().write("<form method=\"post\">");
    resp.getWriter().write("Email: <input type =\"text\" name=\"email\"></input>");
    resp.getWriter().write("<input type=\"submit\"></input>");
    resp.getWriter().write("</form>");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    resp.setContentType("text/plain");
    
    String emailS = checkNotNull(req.getParameter("email"));
    resp.getWriter().write(emailS + "\n");

    EmailAddress email = null;
    try {
      email = new EmailAddress(emailS);
      resp.getWriter().write("OK - " + email.getEmail());
    } catch (IllegalArgumentException e) {
      resp.getWriter().write("FAIL - " + emailS);
    }
    
  }

}
