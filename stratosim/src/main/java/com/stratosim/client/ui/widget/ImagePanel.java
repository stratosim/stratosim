package com.stratosim.client.ui.widget;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ImagePanel extends Composite {
  private SimplePanel panel;
  private Widget errorWidget;

  public ImagePanel(Image image) {
    Label errorLabel = new Label(":(");
    errorLabel.setHorizontalAlignment(Label.ALIGN_CENTER);
    errorWidget = errorLabel;

    panel = new SimplePanel();
    initWidget(panel);

    image.addErrorHandler(imageErrorHandler);

    // TODO(tpondich): Prefetch.
    panel.add(image);

    setStylePrimaryName("stratosim-ImagePanel");
    image.setStylePrimaryName("stratosim-ImagePanel-Image");
  }

  private ErrorHandler imageErrorHandler = new ErrorHandler() {
    @Override
    public void onError(ErrorEvent event) {
      panel.clear();
      panel.add(errorWidget);
    }
  };
}
