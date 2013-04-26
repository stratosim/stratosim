package com.stratosim.server.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import com.stratosim.server.persistence.schema.CustomKeyFactory;
import com.stratosim.shared.filemodel.CircuitHash;

public class GrabDataServlet extends HttpServlet {
  
  private static final Logger logger = Logger.getLogger(GrabDataServlet.class.getCanonicalName());

  private static final long serialVersionUID = -4951287424230986168L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());

    resp.setContentType("text/html");

    PrintWriter out = resp.getWriter();
    out.println("<html>"
          + "<head><title>Admin - grab data</title></head>"
          + "<body>"
          + "<div>"
          + "<form action=\"#\" method=\"post\">"
          + "Number of files: <input type=\"text\" name=\"number\"/>"
          + "<input type=\"submit\" value=\"Download\"/>"
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
    
    int numFiles = Integer.parseInt(req.getParameter("number"));
    Preconditions.checkArgument(numFiles > 0, "specify number > 0");

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ZipOutputStream zipOut = new ZipOutputStream(byteOut);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("circuitPs");
    PreparedQuery prepared = datastore.prepare(query);
    FetchOptions options = FetchOptions.Builder.withLimit(20);
    
    for (Entity psEntity : prepared.asIterable(options)) {
      try {
        Blob psData = (Blob) psEntity.getProperty("data");
        CircuitHash hash = new CircuitHash(psEntity.getKey().getName());
        logger.info("hash: " + hash.get());
        Entity spiceEntity = datastore.get(
            CustomKeyFactory.circuitSpiceKey(hash).dsKey());
        Blob spiceData = (Blob) spiceEntity.getProperty("data");
        
        zipOut.putNextEntry(new ZipEntry(hash.get() + ".ps"));
        zipOut.write(psData.getBytes());
        zipOut.closeEntry();
  
        zipOut.putNextEntry(new ZipEntry(hash.get() + ".spice"));
        zipOut.write(spiceData.getBytes());
        zipOut.closeEntry();
      } catch (EntityNotFoundException ex) {
        logger.log(Level.SEVERE, "entity not found", ex);
      }
    }
    zipOut.close();

    resp.setHeader("Content-Disposition", "attachment; filename=stratosim-grab-data.zip");
    resp.setContentType("application/zip");
    resp.getOutputStream().write(byteOut.toByteArray());
  }

}
