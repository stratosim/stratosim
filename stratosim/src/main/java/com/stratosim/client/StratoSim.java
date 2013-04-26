package com.stratosim.client;

import static com.google.gwt.core.client.GWT.getModuleBaseURL;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StratoSim implements EntryPoint {
  AppController appController;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    // get XSRF token, then proceed
    XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync) GWT.create(XsrfTokenService.class);
    ((ServiceDefTarget) xsrf).setServiceEntryPoint(getModuleBaseURL() + "xsrf-service");
    xsrf.getNewXsrfToken(new AsyncCallback<XsrfToken>() {

      @Override
      public void onSuccess(XsrfToken token) {
        StratoSimStatic.setXsrfToken(token);
        appController = new AppController();
        appController.go(RootLayoutPanel.get());
      }

      @Override
      public void onFailure(Throwable caught) {
        // TODO(josh): refresh? handle in a different way?
        throw new IllegalStateException(caught);
      }

    });
  }

}
