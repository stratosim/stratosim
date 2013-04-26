package com.stratosim.server.admin.scripts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class DatastoreClean_null_v2012_02_01 extends HttpServlet {

  private static final long serialVersionUID = -4026055809293005022L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    resp.setContentType("text/html");

    PrintWriter out = resp.getWriter();
    out.println("<html>"
          + "<head><title>Admin - clean null v2012_02_01</title></head>"
          + "<body>"
          + "<p>Click to confirm:</p>"
          + "<div>"
          + "<form action=\"#\" method=\"post\">"
          + "<input type=\"submit\" value=\"Clean\"/>"
          + "</form>"
          + "</div>");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());
    
    resp.setContentType("text/plain");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    boolean successful = false;
    TransactionOptions options = TransactionOptions.Builder.withXG(true);
    Transaction txn = datastore.beginTransaction(options);
    
    try {
      cleanEntities(datastore, txn, new Query("fileRole"));
      cleanEntities(datastore, txn, new Query("userFile"));
          
      successful = true;

    } finally {
      if (successful) {
        txn.commit();
        resp.getWriter().println("SUCCESS!");
      } else {
        txn.rollback();
        resp.getWriter().println("FAILED!");
      }
    }
  }
  
  private static void cleanEntities(DatastoreService datastore, Transaction txn, Query query) {
    for (Entity entity : datastore.prepare(query).asIterable()) {
      if (cleanEntity(entity)) {
        datastore.put(txn, entity);
      }
    }
  }
  
  /**
   * Removes NONE from the properties of the entity.
   * @param entity The entity.
   * @return {@code true} if the entity was modified.
   */
  private static boolean cleanEntity(Entity entity) {
    List<String> toRemove = Lists.newArrayList();
    boolean modified = false;
    
    for (Map.Entry<String, Object> entry : entity.getProperties().entrySet()) {
      if (entry.getValue() == null) {
        modified = true;
        toRemove.add(entry.getKey());
      }
    }
    
    for (String key : toRemove) {
      entity.removeProperty(key);
    }
    
    return modified;
  }
  
}
