package com.stratosim.server.admin;

import static com.google.common.collect.Iterables.getFirst;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Instant;

import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.stratosim.server.persistence.AdminPersistenceLayer;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class ListUserDataServlet extends HttpServlet {

  private static final long serialVersionUID = 8221845039514222732L;

  private static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().trimResults();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    LowercaseEmailAddress user = null;
    FileKey fileKey = null;

    resp.setContentType("text/html");
    if (req.getPathInfo() != null) {
      ImmutableList<String> args = ImmutableList.copyOf(SPLITTER.split(req.getPathInfo()));
      if (args.size() > 0) {
        user = new LowercaseEmailAddress(args.get(0));
      }
      if (args.size() > 1) {
        fileKey = new FileKey(args.get(1));
      }
    }

    if (user == null){
      printUsers(resp.getWriter(), req.getRequestURI());
    } else if (fileKey == null) {
      Preconditions.checkNotNull(user);
      printFiles(resp.getWriter(), user, req.getRequestURI());
    } else {
      Preconditions.checkNotNull(user);
      Preconditions.checkNotNull(fileKey);
      printVersions(resp.getWriter(), fileKey);
    }
  }

  private static void printUsers(PrintWriter out, String urlBase) {
    AdminPersistenceLayer layer = PersistenceLayerFactory.createAdminLayer();

    out.println("<html>"
          + "<head><title>Admin - list user data - all users</title></head>"
          + "<body>");
    out.println("<table>");

    Ordering<LowercaseEmailAddress> alpha = Ordering.natural()
        .onResultOf(new Function<LowercaseEmailAddress, String>() {
          @Override
          public String apply(LowercaseEmailAddress user) {
            return user.getEmail();
          }
        });
    for (LowercaseEmailAddress user : ImmutableSortedSet.copyOf(alpha, layer.listAllUsers())) {
      out.println("<tr>");
      out.println("<td>");
      out.println(MessageFormat.format("<a href=\"{0}\"/>{1}</a>",
          joinPaths(urlBase, user.getEmail()), user.getEmail()));
      out.println("</td>");
      out.println("</tr>");
    }

    out.println("</table>");
    out.println("</body></html>");
  }

  private static void printFiles(PrintWriter out, LowercaseEmailAddress user, String urlBase) {
    AdminPersistenceLayer layer = PersistenceLayerFactory.createAdminLayer();

    out.println("<html>"
          + "<head><title>Admin - list user data - user files</title></head>"
          + "<body>");
    out.println("<table>");

    out.println("<tr>");
    out.println("<th>File Key</th>");
    out.println("<th>Role</th>");
    out.println("<th>Open File</th>");
    out.println("<th>Open Latest Version</th>");
    out.println("<th>Latest Version Time</th>");
    out.println("</tr>");

    for (Map.Entry<FileKey, FileRole> entry : layer.getUserPermissions(user).entrySet()) {
      Map.Entry<Instant, VersionMetadataPb> firstVersion = getFirst(
          layer.getVersionProtos(entry.getKey()).descendingMap().entrySet(), null);
      out.println("<tr>");
      out.println(MessageFormat.format("<td><a href=\"{0}\"/>{1}</a></td>",
          joinPaths(urlBase, entry.getKey().get()), entry.getKey().get()));
      out.println("<td>" + entry.getValue() + "</td>");
      out.println(MessageFormat.format("<td><a href=\"{0}\">Open {1}</a>",
          joinPaths("/app/#file", entry.getKey().get()), entry.getKey().get()));
      out.println(MessageFormat.format("<td><a href=\"{0}\">Open {1}</a>",
          joinPaths("/app/#version", entry.getKey().get(),
              Long.toString(firstVersion.getKey().getMillis())),
          Long.toString(firstVersion.getKey().getMillis())));
      out.println("<td>" + firstVersion.getKey() + "</td>");

      out.println("</tr>");
    }

    out.println("</table>");
    out.println("</body></html>");
  }

  private static void printVersions(PrintWriter out, FileKey fileKey) throws IOException {
    AdminPersistenceLayer layer = PersistenceLayerFactory.createAdminLayer();

    out.println("<html>"
          + "<head><title>Admin - list user data - versions</title></head>"
          + "<body>");
    out.println("<table>");

    out.println("<tr>");
    out.println("<th>Open Version</th>");
    out.println("<th>Formatted version</th>");
    out.println("<th>Name</th>");
    out.println("<th>Saver Email</th>");
    out.println("<th>Circuit hash</th>");
    out.println("</tr>");

    for (Map.Entry<Instant, VersionMetadataPb> entry :
        layer.getVersionProtos(fileKey).entrySet()) {
      out.println("<tr>");
      out.println(MessageFormat.format("<td><a href=\"{0}\">Open {1}</a>",
          joinPaths("/app/#version", fileKey.get(), Long.toString(entry.getKey().getMillis())),
          Long.toString(entry.getKey().getMillis())));
      out.println("<td>" + entry.getKey() + "</td>");
      out.println("<td>" + entry.getValue().getName() + "</td>");
      out.println("<td>" + entry.getValue().getSaverEmail() + "</td>");
      out.println("<td>" + entry.getValue().getCircuitHash() + "</td>");
      out.println("</tr>");
    }

    out.println("</table>");
    out.println("</body></html>");
  }

  private static final String joinPaths(String... parts) {
    StringBuilder builder = new StringBuilder();
    for (String part : parts) {
      builder.append(part);
      if (!part.endsWith("/")) {
        builder.append("/");
      }
    }
    return builder.toString();
  }

}
