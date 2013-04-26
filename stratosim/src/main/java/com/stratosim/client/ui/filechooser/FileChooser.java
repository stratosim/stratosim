package com.stratosim.client.ui.filechooser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.stratosim.client.ui.widget.GridList;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.VersionMetadata;

public class FileChooser extends ResizeComposite
    implements
      HasValueChangeHandlers<FileKey> {

  @UiField
  GridList<VersionMetadata> grid;

  private static FileChooserUiBinder uiBinder = GWT
      .create(FileChooserUiBinder.class);

  interface FileChooserUiBinder extends UiBinder<Widget, FileChooser> {}

  public FileChooser() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void clear() {
    grid.clear();
  }

  public void add(VersionMetadata file, Image thumbnail) {
    if (thumbnail != null) {
      thumbnail.setStylePrimaryName("stratosim-ImagePanel-Image");
      grid.add(thumbnail, thumbnail, file.getName(), file.getName(), file);
    } else {
      grid.add("Preview Not Available", file.getName(), file);
    }
  }

  @UiHandler("grid")
  void onGridChange(ChangeEvent event) {
    fireValueChangeEvent(grid.getSelectedItem().getFileKey());
  }

  private void fireValueChangeEvent(FileKey value) {
    ValueChangeEvent.fire(this, value);
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FileKey> handler) {
    return super.addHandler(handler, ValueChangeEvent.getType());
  }

}
