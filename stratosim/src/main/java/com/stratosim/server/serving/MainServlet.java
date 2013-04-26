package com.stratosim.server.serving;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.stratosim.server.ConfigFactory;
import com.stratosim.server.HashUtils;
import com.stratosim.server.UsersHelper;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.AppPaths;
import com.stratosim.shared.DirectClientData;
import com.stratosim.shared.PersistenceException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MainServlet extends AbstractStratoSimServlet {
  private static final long serialVersionUID = -6588037237080771792L;

  /**
   * Url Format: /[?cuid=$cuid]
   */
  @Override
  protected void doHandledGet(HttpServletRequest request, HttpServletResponse resp)
      throws IOException, AccessException, PersistenceException, TemplateException {

    Map<String, String> root = Maps.newHashMap();
    root.put("topLevel", "app");
    root.put("requestURI", request.getRequestURI());
    
    Configuration config = ConfigFactory.get(getServletContext());

    if (!UsersHelper.isUser(request)) {
      String cookieUID = "" + HashUtils.secureRandom().nextInt();
      String janrainUrl =
          request.getRequestURL().toString().replace(request.getRequestURI(), "")
              + "/login/janrain";
      String stratosimAccountUrl = "/login/stratosim";

      resp.setHeader("Content-Disposition", "inline");
      resp.setContentType("text/html");

      root.put("cookieUID", cookieUID);
      root.put("janrainUrl", janrainUrl);
      root.put("stratosimAccountUrl", stratosimAccountUrl);

      Template template = config.getTemplate("MainServletLoginForm.html");

      PrintWriter out = resp.getWriter();
      template.process(root, out);
      out.flush();

    } else {
      Cookie[] list = request.getCookies();
      String cookieUID = request.getParameter("cuid");
      if (list != null && cookieUID != null) {
        for (int i = 0; i < list.length; i++) {
          if (list[i].getName().equals("cuid_" + cookieUID)) {
            String cookieName = list[i].getName();
            String cookieValue = list[i].getValue();
            if (cookieValue != null) {
              Cookie clearCookie = new Cookie(cookieName, "");
              clearCookie.setMaxAge(0);
              resp.addCookie(clearCookie);
              resp.sendRedirect(AppPaths.APP_PATH + "/#" + cookieValue);
              return;
            }
          }
        }

      } else {
        DirectDataImpl directDataImpl = new DirectDataImpl(request);
        DirectClientData directData = directDataImpl.getData();
        String directDataString = DirectDataUtils.getDirectDataString(directData);
        
        root.put("directDataString", directDataString);

        resp.setHeader("Content-Disposition", "inline");
        resp.setContentType("text/html");

        Template template = config.getTemplate("MainServlet.html");

        PrintWriter out = resp.getWriter();
        template.process(root, out);
        out.flush();

      }
    }
  }

}
