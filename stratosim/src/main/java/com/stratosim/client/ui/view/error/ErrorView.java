package com.stratosim.client.ui.view.error;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.stratosim.client.ui.presenter.error.ErrorPresenter;

public class ErrorView extends Composite implements ErrorPresenter.Display {

  private static ErrorViewUiBinder uiBinder = GWT.create(ErrorViewUiBinder.class);

  interface ErrorViewUiBinder extends UiBinder<Widget, ErrorView> {}

  @UiField
  Label errorMessageLabel;

  public ErrorView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public void setPresenter(ErrorPresenter presenter) {
    // do nothing
  }

  @Override
  public void setErrorMessageText(String text) {
    errorMessageLabel.setText(text);
  }

}
