package com.stratosim.server.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.stratosim.server.persistence.AdminPersistenceLayer;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class ShowPermissionsServlet extends HttpServlet {

  private static final long serialVersionUID = 8221845039514222732L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    resp.setContentType("text/html");

    PrintWriter out = resp.getWriter();
    out.println("<html>" + "<head><title>Admin - show permissions</title></head>" + "<body>"
        + "<div>" + "<form action=\"#\" method=\"post\">" + "<div>Enter one of:</div>"
        + "<div>Username: <input type=\"text\" name=\"user\"/></div>"
        + "<div>File key: <input type=\"text\" name=\"fileKey\"/></div>"
        + "<input type=\"submit\" value=\"Show permission\"/>" + "</form>" + "</div>" + "</body>"
        + "</html>");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    String userString = req.getParameter("user");
    String fileKeyString = req.getParameter("fileKey");

    AdminPersistenceLayer layer = PersistenceLayerFactory.createAdminLayer();

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    out.println("<html>" + "<head><title>Admin - list versions</title></head>" + "<body>");

    if (userString != null && !userString.isEmpty()) {
      LowercaseEmailAddress user = new LowercaseEmailAddress(userString);
      ImmutableMap<FileKey, FileRole> roles = layer.getUserPermissions(user);

      out.println("<h2>User: " + user + "</h2>");
      out.println("<table>");

      out.println("<tr>");
      out.println("<th>FileKey</th>");
      out.println("<th>Role</th>");
      out.println("</tr>");

      for (Map.Entry<FileKey, FileRole> entry : roles.entrySet()) {
        out.println("<tr>");
        out.println("<td>" + entry.getKey().get() + "</td>");
        out.println("<td>" + entry.getValue() + "</td>");
        out.println("</tr>");
      }

      out.println("</table>");
      out.println("</body></html>");

    } else if (fileKeyString != null && !fileKeyString.isEmpty()) {
      FileKey fileKey = new FileKey(fileKeyString);
      try {
        ImmutableMap<LowercaseEmailAddress, FileRole> roles = layer.getFilePermissions(fileKey);

        out.println("<h2>File Key: " + fileKey.get() + "</h2>");
        out.println("<table>");

        out.println("<tr>");
        out.println("<th>User</th>");
        out.println("<th>Role</th>");
        out.println("</tr>");

        for (Map.Entry<LowercaseEmailAddress, FileRole> entry : roles.entrySet()) {
          out.println("<tr>");
          out.println("<td>" + entry.getKey() + "</td>");
          out.println("<td>" + entry.getValue() + "</td>");
          out.println("</tr>");
        }

      } catch (PersistenceException e) {
        throw new IllegalStateException(e);
      }

      out.println("</table>");
      out.println("</body></html>");

    } else {
      out.println("User or file key must be specified.");
    }

  }
}
