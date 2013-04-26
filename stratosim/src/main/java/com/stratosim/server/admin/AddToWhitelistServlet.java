package com.stratosim.server.admin;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class AddToWhitelistServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(AddToWhitelistServlet.class
      .getCanonicalName());

  private static final long serialVersionUID = 8221845039514222732L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    checkState(userService.isUserAdmin());

    resp.setContentType("text/html");

    PrintWriter out = resp.getWriter();
    out.println("<html>" + "<head><title>Admin - add to whitelist</title></head>" + "<body>"
        + "<p>Enter emails to add to whitelist (comma or newline separated):</p>" + "<div>"
        + "<form action=\"#\" method=\"post\">"
        + "<textarea style=\"width:500px; height: 500px;\" name=\"users\"></textarea>"
        + "<br /><input type=\"submit\" value=\"Add\"/>" + "</form>" + "</div>" + "</body>"
        + "</html>");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    checkState(userService.isUserAdmin());

    resp.setContentType("text/plain");

    logger.log(Level.INFO, "Users being added by: " + userService.getCurrentUser());

    String usersString = req.getParameter("users");
    List<LowercaseEmailAddress> users = Lists.newArrayList();

    // Perform basic email validation and convert emails to
    // lowercase. Will split input by , or newline.
    Iterable<String> emails =
        Splitter.on(CharMatcher.anyOf(",\n\r")).omitEmptyStrings().trimResults().split(usersString);

    for (String email : emails) {
      try {
        users.add(new LowercaseEmailAddress(email));

      } catch (Exception e) {
        logger.log(Level.WARNING, email + "\tINVALID\n");
        resp.getWriter().write(email + "\tINVALID\n");

      }
    }

    String status = WhitelistHelper.addToWhitelist(getServletContext(), users);
    resp.getWriter().write(status);
  }

}
