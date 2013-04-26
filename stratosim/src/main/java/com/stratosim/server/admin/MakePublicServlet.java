package com.stratosim.server.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.filemodel.FileKey;

public class MakePublicServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(MakePublicServlet.class
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
          + "<head><title>Admin - make public</title></head>"
          + "<body>"
          + "<p>Enter file keys make public (comma separated):</p>"
          + "<div>"
          + "<form action=\"#\" method=\"post\">"
          + "File keys: <input type=\"text\" name=\"fileKeys\"/>"
          + "<input type=\"submit\" value=\"Make Public\"/>"
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

    String fileKeysString = req.getParameter("fileKeys");
    List<FileKey> fileKeys = Lists.newArrayList();
    for (String keyString : fileKeysString.split(",")) {
      fileKeys.add(new FileKey(keyString));
    }

    logger.info("Making files public from admin page: "
        + "fks=" + fileKeys.toString() + ", "
        + "by " + current.getEmail());

    resp.setContentType("text/plain");

    boolean success = true;
    for (FileKey fileKey : fileKeys) {
      try {
        PersistenceLayerFactory.createAdminLayer().makePublic(fileKey);
      } catch (Exception ex) {
        success = false;
        resp.getWriter().write("error in making public fk=" + fileKey.get() + ":\n");
        ex.printStackTrace(resp.getWriter());
      }
    }
    if (success) {
      resp.getWriter().write("SUCCESS!");
    }
  }

}
