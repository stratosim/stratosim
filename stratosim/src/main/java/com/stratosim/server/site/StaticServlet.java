package com.stratosim.server.site;

import static com.stratosim.server.HttpCacheControlHelper.makeCachedInternalForever;
import static com.stratosim.server.HttpCacheControlHelper.makeCachedPublicLimited;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.stratosim.server.ConfigFactory;
import com.stratosim.server.serving.AbstractStratoSimServlet;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class StaticServlet extends AbstractStratoSimServlet {

  private static final long serialVersionUID = 3696874694365288265L;

  private final Splitter SPLITTER = Splitter.on(CharMatcher.anyOf("/_")).omitEmptyStrings();

  /**
   * Used to serve static pages with simple templates. The response cache will optimize these
   * responses to be ~1.5x a static request and also will enable AppEngine edge caching.
   * 
   * A URI of the form /foo/bar/fee_phi_pho_phum will look for the template
   * FooBarFeePhiPhoPhumStatic.html.
   */
  @Override
  protected void doHandledGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException, TemplateException {

    Iterable<String> parts = SPLITTER.split(request.getRequestURI());

    StringBuffer templateName = new StringBuffer();
    for (String part : parts) {
      templateName.append(Character.toUpperCase(part.charAt(0)));
      templateName.append(part.substring(1));
    }
    templateName.append("Static.html");

    String topLevel = "";
    for (String part : parts) {
      topLevel = part;
      break;
    }

    response.setHeader("Content-Disposition", "inline");
    response.setContentType("text/html");

    Configuration config = ConfigFactory.get(getServletContext());
    Template template = config.getTemplate(templateName.toString());
    Map<String, String> root = Maps.newHashMap();

    root.put("topLevel", topLevel);
    root.put("requestURI", request.getRequestURI());

    makeCachedPublicLimited(response);
    makeCachedInternalForever(response);

    PrintWriter out = response.getWriter();
    template.process(root, out);
    out.flush();
  }

}
