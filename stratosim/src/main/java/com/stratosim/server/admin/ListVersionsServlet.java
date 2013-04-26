package com.stratosim.server.admin;

import static com.google.common.collect.Iterables.getOnlyElement;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;

import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSortedMap;
import com.stratosim.server.persistence.AdminPersistenceLayer;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.filemodel.FileKey;

public class ListVersionsServlet extends HttpServlet {

  private static final long serialVersionUID = 8221845039514222732L;
  
  private static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().trimResults();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());
    
    if (req.getPathInfo() != null) {
      FileKey fileKey = new FileKey(getOnlyElement(SPLITTER.split(req.getPathInfo())));
      printVersions(resp, fileKey);
    } else {
      resp.setContentType("text/html");
  
      PrintWriter out = resp.getWriter();
      out.println("<html>"
            + "<head><title>Admin - list versions</title></head>"
            + "<body>"
            + "<p>Enter file key:</p>"
            + "<div>"
            + "<form action=\"#\" method=\"post\">"
            + "File key: <input type=\"text\" name=\"fileKey\"/>"
            + "<input type=\"submit\" value=\"List Versions\"/>"
            + "</form>"
            + "</div>"
            + "</body>"
            + "</html>");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    String fileKeyString = req.getParameter("fileKey");
    FileKey fileKey = new FileKey(fileKeyString);
    printVersions(resp, fileKey);
  }
  
  private static void printVersions(HttpServletResponse resp, FileKey fileKey) throws IOException {
    AdminPersistenceLayer layer = PersistenceLayerFactory.createAdminLayer();
    ImmutableSortedMap<Instant, VersionMetadataPb> versions = layer.getVersionProtos(fileKey);
    
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    out.println("<html>"
          + "<head><title>Admin - list versions</title></head>"
          + "<body>");
    out.println("<h2>File Key: " + fileKey.get() + "</h2>");
    out.println("<table>");
    
    out.println("<tr>");
    out.println("<th>Version</th>");
    out.println("<th>Formatted version</th>");
    out.println("<th>Name</th>");
    out.println("<th>Saver Email</th>");
    out.println("<th>Circuit hash</th>");
    out.println("</tr>");
    
    for (Instant instant : versions.keySet()) {
      VersionMetadataPb pb = versions.get(instant);
      out.println("<tr>");
      out.println("<td>" + instant.getMillis() + "</td>");
      out.println("<td>" + instant + "</td>");
      out.println("<td>" + pb.getName() + "</td>");
      out.println("<td>" + pb.getSaverEmail() + "</td>");
      out.println("<td>" + pb.getCircuitHash() + "</td>");
      out.println("</tr>");
    }
    
    out.println("</table>");
    out.println("</body></html>");
  }

}
