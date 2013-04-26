package com.stratosim.server.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

public class WhitelistCronServlet extends HttpServlet {
  private static final long serialVersionUID = -4724074854825810452L;
  
  /**
   * /c/whitelist-cron-servlet/[number to add]
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    String pathInfo = req.getPathInfo();
    int newUserLimit = Integer.parseInt(pathInfo.substring(1));
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Query unapprovedRequestsQuery =
        new Query("request").setFilter(new FilterPredicate("approved", Query.FilterOperator.EQUAL,
            false));

    List<Entity> approvedRequests = new ArrayList<Entity>();

    List<Entity> unapprovedRequests =
        datastore.prepare(unapprovedRequestsQuery).asList(
            FetchOptions.Builder.withLimit(newUserLimit));
    
    List<LowercaseEmailAddress> usersToWhiteList = new ArrayList<LowercaseEmailAddress>();

    for (Entity request : unapprovedRequests) {
      Email email = (Email) request.getProperty("email");
      request.setProperty("approved", true);
      approvedRequests.add(request);
      usersToWhiteList.add(new LowercaseEmailAddress(email.getEmail()));
    }

    WhitelistHelper.addToWhitelist(getServletContext(), usersToWhiteList);

    datastore.put(approvedRequests);
  }

}
