package com.stratosim.server.accounts;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.stratosim.server.ConfigFactory;
import com.stratosim.server.UsersHelper;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.serving.AbstractStratoSimServlet;
import com.stratosim.shared.AccessException;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class StratoSimChangePasswordServlet extends AbstractStratoSimServlet {
  private static final long serialVersionUID = -5746678995368093751L;

  private static final Logger logger = Logger.getLogger(StratoSimChangePasswordServlet.class
      .getCanonicalName());
  
  @Override
  protected void doHandledPost(HttpServletRequest request, HttpServletResponse response)
      throws IllegalArgumentException, PersistenceException, IOException, TemplateException {

    String email = checkNotNull(request.getParameter("email"));
    String oldhash = checkNotNull(request.getParameter("oldhash"));
    String newhash = checkNotNull(request.getParameter("newhash"));

    response.setHeader("Content-Disposition", "inline");
    response.setContentType("text/html");

    Configuration config = ConfigFactory.get(getServletContext());

    Template template;

    Map<String, String> root = Maps.newHashMap();
    root.put("changePasswordUrl", "/accounts/change-password");

    if (PersistenceLayerFactory.createAccountsLayer(new LowercaseEmailAddress(email))
        .isValidAccount(oldhash)) {

      PersistenceLayerFactory.createAccountsLayer(new LowercaseEmailAddress(email))
          .putAccount(newhash);

      logger.log(Level.INFO, "password changed");
      
      template = config.getTemplate("ChangePasswordServletDone.html");

    } else {
      String username = UsersHelper.getCurrentUser(request).getEmail();
      root.put("username", username);

      logger.log(Level.WARNING, "invalid account");
      
      response.sendRedirect("/accounts/change-password?stratosim_invalid");
      return;
    }

    PrintWriter out = response.getWriter();
    template.process(root, out);
    out.flush();
  }

  @Override
  protected void doHandledGet(HttpServletRequest request, HttpServletResponse resp)
      throws IOException, AccessException, PersistenceException, TemplateException {

    resp.setHeader("Content-Disposition", "inline");
    resp.setContentType("text/html");

    Configuration config = ConfigFactory.get(getServletContext());

    Template template = config.getTemplate("ChangePasswordServlet.html");
    Map<String, String> root = Maps.newHashMap();

    root.put("username", UsersHelper.getCurrentUser(request).getEmail());
    root.put("changePasswordUrl", "/accounts/change-password");

    PrintWriter out = resp.getWriter();
    template.process(root, out);
    out.flush();
  }
}
