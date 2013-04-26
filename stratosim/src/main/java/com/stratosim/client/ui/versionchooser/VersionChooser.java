package com.stratosim.client.ui.versionchooser;

import static com.google.gwt.i18n.shared.DateTimeFormat.getFormat;
import static com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.DATE_MEDIUM;
import static com.stratosim.shared.ShareURLHelper.getCircuitDownloadServiceUrl;
import static com.stratosim.shared.filemodel.DownloadFormat.THUMBNAIL;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.stratosim.client.ui.widget.GridList;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class VersionChooser extends ResizeComposite
    implements
      HasValueChangeHandlers<VersionMetadataKey> {

  @UiField
  GridList<VersionMetadata> grid;

  @UiField
  Image thumbnailImage;
  @UiField
  Label nameLabel;
  @UiField
  Label dateLabel;

  private static VersionChooserPanelUiBinder uiBinder = GWT
      .create(VersionChooserPanelUiBinder.class);

  interface VersionChooserPanelUiBinder extends UiBinder<Widget, VersionChooser> {}

  public VersionChooser() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void clear() {
    grid.clear();
    thumbnailImage.setUrl("");
    nameLabel.setText("");
  }

  private void updatePreviewFromSelectedGridItem() {
    thumbnailImage.setUrl(getCircuitDownloadServiceUrl(grid.getSelectedItem().getVersionKey(), THUMBNAIL));
    nameLabel.setText(grid.getSelectedItem().getName());
    dateLabel.setText(getFormat(DATE_MEDIUM).format(grid.getSelectedItem().getDate()));
  }

  public void add(VersionMetadata version) {

    grid.add(getFormat(DATE_MEDIUM).format(version.getDate()) + " - " + version.getName(), version);

    if (grid.getSelectedIndex() == 0) {
      updatePreviewFromSelectedGridItem();
    }

  }

  @UiHandler("grid")
  void onGridChange(ChangeEvent event) {
    updatePreviewFromSelectedGridItem();
  }

  @UiHandler("doneButton")
  void onDoneButtonClick(ClickEvent event) {
    fireValueChangeEvent(grid.getSelectedItem().getVersionKey());
  }

  private void fireValueChangeEvent(VersionMetadataKey value) {
    ValueChangeEvent.fire(this, value);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<VersionMetadataKey> handler) {
    return super.addHandler(handler, ValueChangeEvent.getType());
  }

}
