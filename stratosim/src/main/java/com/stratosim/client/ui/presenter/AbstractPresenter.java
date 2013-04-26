package com.stratosim.client.ui.presenter;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.stratosim.client.StratoSimStatic;

public abstract class AbstractPresenter implements Presenter {
  private List<HandlerRegistration> registrations;

  public AbstractPresenter() {
    registrations = Lists.newArrayList();
  }

  // TODO(josh): synchronized?
  protected <Handler> void addHandler(Type<Handler> type, Handler handler) {
    registrations.add(StratoSimStatic.getEventBus().addHandler(type, handler));
  }

  // TODO(josh): synchronized?
  @Override
  public void unregisterAll() {

    for (HandlerRegistration registration : registrations) {
      registration.removeHandler();
    }

    registrations = Lists.newArrayList();
  }
}
