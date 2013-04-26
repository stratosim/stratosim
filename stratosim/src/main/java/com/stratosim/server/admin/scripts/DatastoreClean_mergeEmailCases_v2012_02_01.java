package com.stratosim.server.admin.scripts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;

public class DatastoreClean_mergeEmailCases_v2012_02_01 extends HttpServlet {

  private static final long serialVersionUID = 4440886991444584683L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    resp.setContentType("text/html");

    PrintWriter out = resp.getWriter();
    out.println("<html>"
          + "<head><title>Admin - merge  v2012_02_01</title></head>"
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
    
    try {
      for (Entity dirty : datastore.prepare(new Query("fileRole")).asIterable()) {
        Entity clean = new Entity(dirty.getKey());
        for (String key : dirty.getProperties().keySet()) {
          String lower = key.toLowerCase();
          clean.setProperty(lower, dirty.getProperty(key));
        }
        datastore.put(clean);
      }
      
      successful = true;
      
    } finally {
      if (successful) {
        resp.getWriter().println("SUCCESS!");
      } else {
        resp.getWriter().println("FAILED!");
      }
    }
  }
  
}
