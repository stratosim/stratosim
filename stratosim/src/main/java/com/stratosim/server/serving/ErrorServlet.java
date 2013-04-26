package com.stratosim.server.serving;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.stratosim.server.ConfigFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;


public class ErrorServlet extends HttpServlet {
  private static final long serialVersionUID = -6588037237080771792L;

  private static final Logger logger = Logger.getLogger(ErrorServlet.class
    .getCanonicalName());
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
    resp.setHeader("Content-Disposition", "inline");
    resp.setContentType("text/html");

    Configuration config = ConfigFactory.get(getServletContext());
    Template template = config.getTemplate("ErrorServlet.html");;
    Map<String, String> root = Maps.newHashMap();
    
    try {      
      if (request.getPathInfo().contains("404")) {
        root.put("error404", "true");
      } else if (request.getPathInfo().contains("403")) {
        root.put("error403", "true");        
      } else if (request.getPathInfo().contains("400")) {
        root.put("error400", "true");        
      } else {
        logger.log(Level.SEVERE, "Invalid Error Code");
        // This is bad, but don't exception and leak a trace.
      }

      PrintWriter out = resp.getWriter();
      template.process(root, out);
      out.flush();

    } catch (Exception ex) {
      logger.log(Level.SEVERE, "", ex);
      
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }

}
