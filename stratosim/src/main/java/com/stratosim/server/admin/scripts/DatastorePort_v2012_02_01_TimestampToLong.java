package com.stratosim.server.admin.scripts;
//package com.stratosim.server.admin.scripts;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.joda.time.Instant;
//
//import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
//import com.stratosim.shared.util.DateFormatting;
//import com.google.appengine.api.datastore.Blob;
//import com.google.appengine.api.datastore.DatastoreService;
//import com.google.appengine.api.datastore.DatastoreServiceFactory;
//import com.google.appengine.api.datastore.Entity;
//import com.google.appengine.api.datastore.Query;
//import com.google.appengine.api.users.UserService;
//import com.google.appengine.api.users.UserServiceFactory;
//import com.google.common.base.Preconditions;
//import com.google.common.collect.Lists;
//
//public class DatastorePort_v2012_02_01_TimestampToLong extends HttpServlet {
//
//  private static final long serialVersionUID = -4026055809293005022L;
//
//  @Override
//  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
//      IOException {
//
//    UserService userService = UserServiceFactory.getUserService();
//    Preconditions.checkState(userService.isUserAdmin());
//
//    resp.setContentType("text/html");
//
//    PrintWriter out = resp.getWriter();
//    out.println("<html>"
//          + "<head><title>Admin - datastore migration v2012_02_01</title></head>"
//          + "<body>"
//          + "<p>Click to confirm:</p>"
//          + "<div>"
//          + "<form action=\"#\" method=\"post\">"
//          + "<input type=\"submit\" value=\"Migrate\"/>"
//          + "</form>"
//          + "</div>");
//    
//    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//    Query query = new Query("fileVersions");
//    
//    out.println("<pre>");
//    for (Entity oldEntity : datastore.prepare(query).asIterable()) {
//      Entity newEntity = new Entity(oldEntity.getKey());
//      out.write(oldEntity.getKey().getName() + ": ");
//      
//      for (String property : oldEntity.getProperties().keySet()) {
//        Instant time = Instant.parse(property, DateFormatting.getFormatter());
//        newEntity.setProperty(
//          Long.toString(time.getMillis()),
//          oldEntity.getProperty(property));
//        
//        VersionMetadataPb pb = VersionMetadataPb.parseFrom(
//          ((Blob) oldEntity.getProperty(property)).getBytes());
//        out.write("{" + Long.toString(time.getMillis()) + ": "
//            + pb.getName() + "}, ");
//      }
//      
//      out.println();
//    }
//    out.println("</pre>");
//
//    out.println("</body></html>");
//  }
//
//  @Override
//  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
//      IOException {
//
//    UserService userService = UserServiceFactory.getUserService();
//    Preconditions.checkState(userService.isUserAdmin());
//    
//    resp.setContentType("text/plain");
//
//    try {
//      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//      Query query = new Query("fileVersions");
//      
//      // Overwrite old entities with new
//      List<Entity> newEntities = Lists.newArrayList();
//      for (Entity oldEntity : datastore.prepare(query).asIterable()) {
//        Entity newEntity = new Entity(oldEntity.getKey());
//        
//        for (String property : oldEntity.getProperties().keySet()) {
//          Instant time = Instant.parse(property, DateFormatting.getFormatter());
//          newEntity.setProperty(
//            Long.toString(time.getMillis()),
//            oldEntity.getProperty(property));
//        }
//        
//        newEntities.add(newEntity);
//      }
//      
//      datastore.put(newEntities);
//      
//      resp.getWriter().println("SUCCESS!");
//      
//    } catch (Exception ex) {
//      resp.getWriter().println("FAILED!");
//      ex.printStackTrace(resp.getWriter());
//    }
//  }
//  
//}
