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
//import com.stratosim.server.Namespace;
//import com.stratosim.server.persistence.AdminPersistenceLayer;
//import com.stratosim.server.persistence.PersistenceLayerFactory;
//import com.stratosim.server.persistence.kinds.PermissionKind;
//import com.stratosim.shared.filemodel.FileKey;
//import com.stratosim.shared.filemodel.FileRole;
//import com.google.appengine.api.NamespaceManager;
//import com.google.appengine.api.datastore.DatastoreService;
//import com.google.appengine.api.datastore.DatastoreServiceFactory;
//import com.google.appengine.api.datastore.Entity;
//import com.google.appengine.api.datastore.Key;
//import com.google.appengine.api.datastore.KeyFactory;
//import com.google.appengine.api.datastore.Query;
//import com.google.appengine.api.users.User;
//import com.google.appengine.api.users.UserService;
//import com.google.appengine.api.users.UserServiceFactory;
//import com.google.common.base.Joiner;
//import com.google.common.base.Preconditions;
//import com.google.common.collect.Lists;
//
//public class DatastorePort_v2012_01_27 extends HttpServlet {
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
//          + "<head><title>Admin - datastore migration v2012_01_27</title></head>"
//          + "<body>"
//          + "<p>Click to confirm:</p>"
//          + "<div>"
//          + "<form action=\"#\" method=\"post\">"
//          + "<input type=\"submit\" value=\"Migrate\"/>"
//          + "</form>"
//          + "</div>");
//    
//    // Get old permissions
//    NamespaceManager.set("");
//    List<Entity> oldPermissions = Lists.newArrayList();
//    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//    Query query = new Query("permission");
//    for (Entity entity : datastore.prepare(query).asIterable()) {
//      oldPermissions.add(entity);
//    }
//    
//    // Save new permissions
//    try {
//      Namespace.setCurrent();
//      for (Entity entity : oldPermissions) {
//        PermissionKind old = new PermissionKind();
//        old.mergeFrom(entity);
//        String[] parts = entity.getKey().getName().split(";");
//        
//        Preconditions.checkState(parts.length == 2);
//        FileKey fileKey = new FileKey(parts[0]);
//        String email = parts[1];
//        
//        User user = new User(email, "gmail.com");
//        FileRole role;
//        if (old.isOwner.get()) {
//          role = FileRole.OWNER;
//        } else if (old.isWriter.get()) {
//          role = FileRole.WRITER;
//        } else if (old.isReader.get()) {
//          role = FileRole.READER;
//        } else {
//          throw new IllegalStateException("unrecognized permissions for " + email);
//        }
//        
//        out.print(email + ", " + fileKey.get() + ", ");
//        List<String> permNames = Lists.newArrayListWithCapacity(3);
//        if (old.isOwner.get()) { permNames.add("owner"); }
//        if (old.isWriter.get()) { permNames.add("writer"); }
//        if (old.isReader.get()) { permNames.add("reader"); }
//        out.print(Joiner.on(":").join(permNames));
//        out.print(" -> ");
//        out.print(user.getEmail() + ", " + fileKey + ", " + role);
//        out.print("<br/>");
//      }
//      
//    } catch (Exception ex) {
//      out.write("FAILED!");
//      ex.printStackTrace(resp.getWriter());
//    }
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
//    // Get old permissions
//    NamespaceManager.set("");
//    List<Entity> oldPermissions = Lists.newArrayList();
//    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//    Query query = new Query("permission");
//    for (Entity entity : datastore.prepare(query).asIterable()) {
//      oldPermissions.add(entity);
//    }
//    
//    // Save new permissions
//    try {
//      Namespace.setCurrent();
//      AdminPersistenceLayer layer = PersistenceLayerFactory.createAdminLayer();
//      for (Entity entity : oldPermissions) {
//        PermissionKind old = new PermissionKind();
//        old.mergeFrom(entity);
//        String[] parts = entity.getKey().getName().split(";");
//        
//        Preconditions.checkState(parts.length == 2);
//        FileKey fileKey = new FileKey(parts[0]);
//        String email = parts[1];
//        
//        User user = new User(email, "gmail.com");
//        FileRole role;
//        if (old.isOwner.get()) {
//          role = FileRole.OWNER;
//        } else if (old.isWriter.get()) {
//          role = FileRole.WRITER;
//        } else if (old.isReader.get()) {
//          role = FileRole.READER;
//        } else {
//          throw new IllegalStateException("unrecognized permissions for " + email);
//        }
//        
//        layer.setPermission(user, fileKey, role);
//      }
//      
//      String[] kinds = { "versionMetadata", "circuitSpice", "circuitPs", "circuitPng",
//                            "circuitPdf", "circuitHDF5", "circuitData" };
//      for (String kind : kinds) {
//        try {
//          copyEntities(kind, resp.getWriter());
//        } catch (Exception ex) {
//          ex.printStackTrace(resp.getWriter());
//        }
//      }
//      
//      resp.getWriter().println("SUCCESS!");
//      
//    } catch (Exception ex) {
//      resp.getWriter().println("FAILED!");
//      ex.printStackTrace(resp.getWriter());
//    }
//  }
//  
//  private void copyEntities(String kind, PrintWriter writer) {
//    NamespaceManager.set("");
//    List<Entity> oldEntities = Lists.newArrayList();
//    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//    Query query = new Query(kind);
//    for (Entity oldEntity : datastore.prepare(query).asIterable()) {
//      oldEntities.add(oldEntity);
//    }
//    writer.println("Read " + oldEntities.size() + " entities of kind " + kind);
//    
//    Namespace.setCurrent();
//    List<Entity> newEntities = Lists.newArrayList();
//    for (Entity oldEntity : oldEntities) {
//      Key oldKey = oldEntity.getKey();
//      String name = oldKey.getName();
//      Entity entity;
//      if (name != null) {
//        Key key = KeyFactory.createKey(kind, name);
//        entity = new Entity(key);
//      } else {
//        entity = new Entity(kind);
//      }
//      entity.setPropertiesFrom(oldEntity);
//      newEntities.add(entity);
//    }
//    datastore.put(newEntities);
//    writer.println("Wrote " + newEntities.size() + " entities of kind " + kind);
//  }
//
//}
