package com.stratosim.server.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class SetPermissionsServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(SetPermissionsServlet.class
      .getCanonicalName());

  private static final long serialVersionUID = 8221845039514222732L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    resp.setContentType("text/html");

    PrintWriter out = resp.getWriter();
    out.println("<html>"
        + "<head><title>Admin - set role</title></head>"
        + "<body>"
        + "<div>"
        + "<form action=\"#\" method=\"post\">"
        + "<div>Username: <input type=\"text\" name=\"user\"/></div>"
        + "<div>File key: <input type=\"text\" name=\"fileKey\"/></div>"
        + "<div>Role:"
        + "<div><input type=\"radio\" name=\"role\" value=\"OWNER\">OWNER</input></div>"
        + "<div><input type=\"radio\" name=\"role\" value=\"WRITER\"/>WRITER</input></div>"
        + "<div><input type=\"radio\" name=\"role\" value=\"READER\">READER</input></div>"
        + "<input type=\"submit\" value=\"Set permission\"/>"
        + "</form>"
        + "</div>"
        + "</body>"
        + "</html>");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    User current = userService.getCurrentUser();

    String userString = req.getParameter("user");
    FileKey fileKey = new FileKey(req.getParameter("fileKey"));
    FileRole role = FileRole.valueOf(req.getParameter("role"));

    logger.info("Adding permission from admin page: "
        + "user=" + userString + ", "
        + "fileKey=" + fileKey.get() + ", "
        + "role=" + role + ", "
        + "by " + current.getEmail());

    LowercaseEmailAddress user = new LowercaseEmailAddress(userString);
    resp.setContentType("text/plain");

    try {
      PersistenceLayerFactory.createAdminLayer().setPermission(user, fileKey, role);
      resp.getWriter().write("SUCCESS!");

    } catch (Exception ex) {
      resp.getWriter().write("FAILED!");
      ex.printStackTrace(resp.getWriter());
    }
  }

}
