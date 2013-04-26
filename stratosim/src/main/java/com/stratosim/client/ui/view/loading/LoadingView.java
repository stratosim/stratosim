package com.stratosim.client.ui.view.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.stratosim.client.ui.presenter.loading.LoadingPresenter;

public class LoadingView extends Composite implements LoadingPresenter.Display {

  private static LoadingViewUiBinder uiBinder = GWT.create(LoadingViewUiBinder.class);

  interface LoadingViewUiBinder extends UiBinder<Widget, LoadingView> {}

  public LoadingView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public void setPresenter(LoadingPresenter presenter) {
    // do nothing
  }

}
