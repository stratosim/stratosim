package com.stratosim.server.admin.scripts;
//package com.stratosim.server.admin.scripts;
//
//import static com.stratosim.server.Namespace.v2012_01_27;
//import static com.stratosim.server.Namespace.v2012_02_01;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.joda.time.Instant;
//import org.joda.time.format.ISODateTimeFormat;
//
//import com.stratosim.server.Namespace;
//import com.stratosim.server.persistence.kinds.FileVersionsKind;
//import com.stratosim.server.persistence.kinds.VersionMetadataKind;
//import com.stratosim.server.persistence.proto.Persistence.VersionMetadataPb;
//import com.stratosim.server.persistence.schema.CustomKeyFactory;
//import com.stratosim.server.persistence.schema.PKey;
//import com.stratosim.shared.filemodel.FileKey;
//import com.google.appengine.api.datastore.DatastoreService;
//import com.google.appengine.api.datastore.DatastoreServiceFactory;
//import com.google.appengine.api.datastore.Entity;
//import com.google.appengine.api.datastore.Key;
//import com.google.appengine.api.datastore.KeyFactory;
//import com.google.appengine.api.datastore.Query;
//import com.google.appengine.api.users.UserService;
//import com.google.appengine.api.users.UserServiceFactory;
//import com.google.common.base.Preconditions;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//
//public class DatastorePort_v2012_02_01 extends HttpServlet {
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
//    // Get old permissions
//    Namespace.v2012_01_27.set();
//
//    // Get old metadata
//    v2012_01_27.set();
//    
//    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//    Query query = new Query("versionMetadata");
//    
//    Map<FileKey, FileVersionsKind> newKinds = Maps.newHashMap();
//    for (Entity entity : datastore.prepare(query).asIterable()) {
//      VersionMetadataKind old = new VersionMetadataKind();
//      old.mergeFrom(entity);
//
//      VersionMetadataPb versionMetadataPb = VersionMetadataPb.newBuilder()
//          .setSaverEmail(old.user.get().getEmail())
//          .setName(old.name.get())
//          .setCircuitHash(old.hash.get().get())
//          .build();
//      
//      FileKey fileKey = old.fileKey.get();
//      if (!newKinds.containsKey(fileKey)) {
//        newKinds.put(fileKey, new FileVersionsKind());
//      }
//      newKinds.get(fileKey).properties.put(old.instant.get(), versionMetadataPb);
//    }
//    
//    // Save new metadata
//    v2012_02_01.set();
//    out.println("<pre>");
//    for (FileKey fileKey : newKinds.keySet()) {
//      PKey<FileVersionsKind> pkey = CustomKeyFactory.fileVersionsKey(fileKey);
//      FileVersionsKind fileVersionsKind = newKinds.get(fileKey);
//      out.print(pkey + ": { ");
//      for (Instant time : fileVersionsKind.properties.keySet()) {
//        out.print(ISODateTimeFormat.basicDateTime().print(time) + ": ");
//        VersionMetadataPb pb = fileVersionsKind.properties.get(time);
//        out.print(pb.getName());
//        out.print(", ");
//      }
//      out.println(" }");
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
//      // Get old metadata
//      v2012_01_27.set();
//      
//      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//      Query query = new Query("versionMetadata");
//      
//      Map<FileKey, FileVersionsKind> newKinds = Maps.newHashMap();
//      for (Entity entity : datastore.prepare(query).asIterable()) {
//        VersionMetadataKind old = new VersionMetadataKind();
//        old.mergeFrom(entity);
//
//        VersionMetadataPb versionMetadataPb = VersionMetadataPb.newBuilder()
//            .setSaverEmail(old.user.get().getEmail())
//            .setName(old.name.get())
//            .setCircuitHash(old.hash.get().get())
//            .build();
//        
//        FileKey fileKey = old.fileKey.get();
//        if (!newKinds.containsKey(fileKey)) {
//          newKinds.put(fileKey, new FileVersionsKind());
//        }
//        newKinds.get(fileKey).properties.put(old.instant.get(), versionMetadataPb);
//      }
//      
//      // Save new metadata
//      v2012_02_01.set();
//      List<Entity> newEntities = Lists.newArrayList();
//      for (FileKey fileKey : newKinds.keySet()) {
//        PKey<FileVersionsKind> pkey = CustomKeyFactory.fileVersionsKey(fileKey);
//        newEntities.add(newKinds.get(fileKey).toEntityWithKey(pkey));
//      }
//      datastore.put(newEntities);
//      
//      String[] sameNameKinds = { "circuitSpice", "circuitPs", "circuitPng", "circuitPdf",
//                                 "circuitHDF5", "circuitData" };
//      for (String kind : sameNameKinds) {
//        try {
//          copyEntities(kind, v2012_01_27, kind, v2012_02_01, resp.getWriter());
//        } catch (Exception ex) {
//          ex.printStackTrace(resp.getWriter());
//        }
//      }
//      
//      copyEntities("fileRoleKind", v2012_01_27, "fileRole", v2012_02_01, resp.getWriter());
//      copyEntities("userFileKind", v2012_01_27, "userFile", v2012_02_01, resp.getWriter());
//      
//      resp.getWriter().println("SUCCESS!");
//      
//    } catch (Exception ex) {
//      resp.getWriter().println("FAILED!");
//      ex.printStackTrace(resp.getWriter());
//    }
//  }
//  
//  private void copyEntities(String sourceKind, Namespace sourceNamespace,
//                            String destKind, Namespace destNamespace,
//                            PrintWriter writer) {
//    sourceNamespace.set();
//    List<Entity> oldEntities = Lists.newArrayList();
//    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//    Query query = new Query(sourceKind);
//    for (Entity oldEntity : datastore.prepare(query).asIterable()) {
//      oldEntities.add(oldEntity);
//    }
//    writer.println("Read " + oldEntities.size() + " entities of kind " + sourceKind);
//    
//    destNamespace.set();
//    List<Entity> newEntities = Lists.newArrayList();
//    for (Entity oldEntity : oldEntities) {
//      Key oldKey = oldEntity.getKey();
//      String name = oldKey.getName();
//      Entity entity;
//      if (name != null) {
//        Key key = KeyFactory.createKey(destKind, name);
//        entity = new Entity(key);
//      } else {
//        entity = new Entity(destKind);
//      }
//      entity.setPropertiesFrom(oldEntity);
//      newEntities.add(entity);
//    }
//    datastore.put(newEntities);
//    writer.println("Wrote " + newEntities.size() + " entities of kind " + destKind);
//  }
//
//}
