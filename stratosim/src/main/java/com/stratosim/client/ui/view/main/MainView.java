package com.stratosim.client.ui.view.main;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.SimulationSettingsPanel;
import com.stratosim.client.ui.devicepanel.DeviceSublist;
import com.stratosim.client.ui.devicepanel.DeviceSublistFactory;
import com.stratosim.client.ui.devicepanel.LeftDevicePanel;
import com.stratosim.client.ui.devicepanel.SingleSublistDisplay;
import com.stratosim.client.ui.filechooser.FileChooser;
import com.stratosim.client.ui.graphexplorepanel.GraphExplorePanel;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.schematiceditor.SchematicPanel;
import com.stratosim.client.ui.sharechooser.ShareChooser;
import com.stratosim.client.ui.versionchooser.VersionChooser;
import com.stratosim.client.ui.widget.BetterTextBox;
import com.stratosim.client.ui.widget.FramePanel;
import com.stratosim.client.ui.widget.GridList;
import com.stratosim.client.ui.widget.MessageText;
import com.stratosim.client.ui.widget.NotificationDialog;
import com.stratosim.shared.circuithelpers.DeviceManager;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.circuitmodel.SimulationSettings;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class MainView extends ResizeComposite implements MainPresenter.Display {
  private static MainUiBinder uiBinder = GWT.create(MainUiBinder.class);

  interface MainUiBinder extends UiBinder<Widget, MainView> {}

  @UiField
  LayoutPanel mainLayout;

  @UiField
  BetterTextBox nameTextBox;

  @UiField
  PushButton newButton;
  @UiField
  PushButton deleteButton;
  @UiField
  PushButton saveButton;
  @UiField
  PushButton copyButton;
  @UiField
  PushButton openButton;
  @UiField
  PushButton versionsButton;
  @UiField
  PushButton shareButton;

  @UiField
  PushButton undoButton;
  @UiField
  PushButton redoButton;

  @UiField
  PushButton zoomInButton;
  @UiField
  PushButton zoomOutButton;
  @UiField
  PushButton zoomFitButton;

  @UiField
  PushButton printButton;
  @UiField
  ToggleButton printButtonMore;
  @UiField
  GridList<DownloadFormat> printSettings;

  @UiField
  PushButton simulateButton;
  @UiField
  ToggleButton simulateButtonMore;
  @UiField
  HTML simulationErrorsLabel;
  @UiField
  SimulationSettingsPanel simulationSettingsPanel;

  @UiField
  Widget readOnlyLabel;

  @UiField
  MessageText messageText;

  @UiField
  FramePanel notificationPanelFrame;
  @UiField
  NotificationDialog notificationPanel;

  @UiField
  LeftDevicePanel devicePanel;
  @UiField
  Widget crackLabel;
  @UiField
  SingleSublistDisplay singleSublistDisplay;

  @UiField
  GraphExplorePanel graphExplorePanel;

  @UiField
  Widget dialogBackPane;

  @UiField
  FramePanel fileChooserPanelFrame;
  @UiField
  FileChooser fileChooser;

  @UiField
  FramePanel versionChooserPanelFrame;
  @UiField
  VersionChooser versionChooser;

  @UiField
  FramePanel sharePanelFrame;
  @UiField
  ShareChooser shareChooser;

  @UiField
  SchematicPanel schematicPanel;

  @UiField
  Label userLabel;
  @UiField
  Anchor accountsLink;

  @UiField
  PushButton tutorialCloseButton;
  @UiField
  Widget tutorialPanel;
  @UiField
  Anchor tutorialLink;

  private MainPresenter presenter;

  public void setPresenter(MainPresenter presenter) {
    this.presenter = presenter;
  }

  public MainView() {
    initWidget(uiBinder.createAndBindUi(this));

    hideAll();
    setParentLayerVisible(devicePanel, true);
    setParentLayerVisible(crackLabel, true);

    setParentLayerVisible(simulationErrorsLabel, false);
    setParentLayerVisible(readOnlyLabel, false);
    setParentLayerVisible(simulationSettingsPanel, false);
    setParentLayerVisible(printSettings, false);

    Window.addWindowClosingHandler(new ClosingHandler() {

      @Override
      public void onWindowClosing(ClosingEvent event) {
        presenter.onWindowClose(event);
      }

    });

    Window.addResizeHandler(new ResizeHandler() {

      @Override
      public void onResize(ResizeEvent event) {
        setCenteredElementsPosition();
      }

    });

    setCenteredElementsPosition();

    String email = StratoSimStatic.getDirectData().getUserEmail().getEmail();
    userLabel.setText(email);
    // TODO(tpondich): Not robust.
    if (StratoSimStatic.getDirectData().isStratoSimAccount()) {
      accountsLink.setVisible(true);
    }
  }

  private void setParentLayerVisible(Widget widget, boolean visible) {
    // TODO(tpondich): Switch to the version in the docs.
    // UIObject.setVisible(mainLayout.getWidgetContainerElement(widget),
    // visible);

    // TODO(tpondich): Make this robust. (Look above)
    if (visible) {
      widget.getElement().getParentElement().setClassName("displayblock");
    } else {
      widget.getElement().getParentElement().setClassName("displaynone");
    }
  }

  private void setCenteredElementPosition(Widget widget, int width, int height, int top) {
    // This is a hack to center consistently.
    // Tried really hard to use css, but could not find a cross browser solution.

    widget.removeFromParent();
    mainLayout.add(widget);
    mainLayout.setWidgetLeftWidth(widget, Window.getClientWidth() / 2 - width / 2, Unit.PX, width,
        Unit.PX);
    mainLayout.setWidgetTopHeight(widget, top, Unit.PX, height, Unit.PX);
  }

  private void setCenteredElementsPosition() {
    // TODO(tpondich): Figure out how to read this from the initial css.

    setCenteredElementPosition(nameTextBox, 320, 50, 0);
    setCenteredElementPosition(messageText, 320, 20, 50);
  }

  @UiFactory
  SingleSublistDisplay makeSingleSublistDisplay() {
    DeviceManager deviceManager = StratoSimStatic.getLocalDeviceManager();

    Map<String, DeviceSublist> sublists = Maps.newHashMap();
    for (String category : deviceManager.getCategories()) {
      DeviceSublist sublist =
          DeviceSublistFactory.create(deviceManager.getDeviceDisplay(category),
              deviceManager.getDeviceTypes(category));
      sublists.put(category, sublist);
    }

    SingleSublistDisplay sublistDisplay = new SingleSublistDisplay(sublists);
    sublistDisplay.showSublist(deviceManager.getCategories().get(0));

    return sublistDisplay;
  }

  @UiHandler("tutorialCloseButton")
  public void onTutorialCloseButtonClick(ClickEvent event) {
    setParentLayerVisible(tutorialPanel, false);
  }

  @UiHandler("tutorialLink")
  public void onTutorialLinkClick(ClickEvent event) {
    setParentLayerVisible(tutorialPanel, true);
  }

  @UiHandler("saveButton")
  public void onSaveButtonClick(ClickEvent event) {
    presenter.onSaveButtonClicked();
  }

  @UiHandler("copyButton")
  public void onCopyButtonClick(ClickEvent event) {
    presenter.onCopyButtonClicked();
  }

  @UiHandler("openButton")
  public void onOpenButtonClick(ClickEvent event) {
    presenter.onOpenButtonClicked();
  }

  @UiHandler("versionsButton")
  public void onVersionsButtonClick(ClickEvent event) {
    presenter.onVersionsButtonClicked();
  }

  @UiHandler("newButton")
  public void onNewButtonClick(ClickEvent event) {
    presenter.onNewButtonClicked();
  }

  @UiHandler("deleteButton")
  public void onDeleteButtonClick(ClickEvent event) {
    presenter.onDeleteButtonClicked();
  }

  @UiHandler("shareButton")
  public void onShareButtonClick(ClickEvent event) {
    presenter.onShareButtonClicked();
  }

  @UiHandler("undoButton")
  public void onUndoButtonClick(ClickEvent event) {
    presenter.onUndoButtonClicked();
  }

  @UiHandler("redoButton")
  public void onRedoButtonClick(ClickEvent event) {
    presenter.onRedoButtonClicked();
  }

  @UiHandler("zoomInButton")
  public void onZoomInButtonClick(ClickEvent event) {
    presenter.onZoomInButtonClicked();
  }

  @UiHandler("zoomOutButton")
  public void onZoomOutButtonClick(ClickEvent event) {
    presenter.onZoomOutButtonClicked();
  }

  @UiHandler("zoomFitButton")
  public void onZoomFitButtonClick(ClickEvent event) {
    presenter.onZoomFitButtonClicked();
  }

  @UiHandler("printButton")
  public void onPrintButtonClick(ClickEvent event) {
    hideRightDropdowns();
    presenter.onPrintButtonClicked(printSettings.getSelectedItem());
  }

  @UiHandler("simulateButton")
  public void onSimulateButtonClick(ClickEvent event) {
    hideRightDropdowns();
    presenter.onSimulateButtonClicked();
  }

  @UiHandler("nameTextBox")
  public void onChange(ChangeEvent event) {
    schematicPanel.getCircuit().setName(nameTextBox.getText());
    schematicPanel.onCircuitChange();
  }

  @Override
  public void clearMessage() {
    messageText.clearMessage();
  }

  @Override
  public void showWorkingMessage(String message) {
    messageText.showMessage(message, Integer.MAX_VALUE, MessageText.WORKING);
  }

  @Override
  public void showDoneMessage(String message) {
    messageText.showMessage(message, 2000, MessageText.DONE);
  }

  @Override
  public void showErrorMessage(String message) {
    messageText.showMessage(message, 5000, MessageText.ERROR);
  }

  @Override
  public void showAlert(String caption, String message) {
    notificationPanelFrame.setTitleLabelText(caption);
    notificationPanel.setNotification(message, NotificationDialog.ERROR, "Ok");
    setParentLayerVisible(dialogBackPane, true);
    setParentLayerVisible(notificationPanelFrame, true);
  }

  @Override
  public void showConfirmation(String caption, String message) {
    notificationPanelFrame.setTitleLabelText(caption);
    notificationPanel.setNotification(message, NotificationDialog.WARNING, "No", "Yes");
    setParentLayerVisible(dialogBackPane, true);
    setParentLayerVisible(notificationPanelFrame, true);
  }

  @UiHandler("notificationPanel")
  public void onNotificationChange(ValueChangeEvent<String> event) {
    if (event.getValue().equals("Ok")) {
      presenter.onAlertDialogChange();
    } else if (event.getValue().equals("Yes")) {
      presenter.onConfirmationDialogChange(true);
    } else if (event.getValue().equals("No")) {
      presenter.onConfirmationDialogChange(false);
    }
  }

  @Override
  public Circuit getCurrentCircuit() {
    return schematicPanel.getCircuit();
  }

  @Override
  public void setCurrentCircuit(Circuit circuit) {
    schematicPanel.setCircuit(circuit);
    // Should this be something the presenter does?
    setControlsFromCurrentCircuit();
  }

  @Override
  public void setControlsFromCurrentCircuit() {
    nameTextBox.setText(schematicPanel.getCircuit().getName());
    SimulationSettings settings = schematicPanel.getCircuit().getSimulationSettings();
    simulationSettingsPanel.setValues(settings);
  }

  @Override
  public void setSimulateButtonEnabled(boolean enabled) {
    simulateButton.setEnabled(enabled);
    simulateButtonMore.setEnabled(enabled);
  }

  @Override
  public void setSimulateButtonDown(boolean down) {
    // TODO(tpondich): Do this manually for the sim button for xbrowser

    if (down) {
      simulateButtonMore.setEnabled(false);
    }
  }

  @UiHandler("devicePanel")
  public void onDevicePanelChange(ChangeEvent event) {
    singleSublistDisplay.showSublist(devicePanel.getSelected());
    presenter.onDevicePanelChange(singleSublistDisplay.getSelected());
  }

  @UiHandler("singleSublistDisplay")
  public void onDeviceSublistPanelChange(ChangeEvent event) {
    presenter.onDevicePanelChange(singleSublistDisplay.getSelected());
  }

  @UiHandler("fileChooser")
  public void onFileListChange(ValueChangeEvent<FileKey> event) {
    presenter.onFileChooserChange(event.getValue());
  }

  @UiHandler("fileChooserPanelFrame")
  public void onFileListClose(CloseEvent<FramePanel> event) {
    presenter.onFileChooserClose();
  }

  @Override
  public void clearFileList() {
    fileChooser.clear();
  }

  @Override
  public void addToFileList(VersionMetadata file, Image thumbnail) {
    fileChooser.add(file, thumbnail);
  }

  @Override
  public void clearVersionList() {
    versionChooser.clear();
  }
  
  @Override
  public void addToVersionList(VersionMetadata version) {
    versionChooser.add(version);
  }
  
  @UiHandler("versionChooserPanelFrame")
  public void onVersionListClose(CloseEvent<FramePanel> event) {
    presenter.onVersionChooserClose();
  }
  
  @UiHandler("versionChooser")
  public void onVersionChooserPanelChange(ValueChangeEvent<VersionMetadataKey> event) {
    presenter.onVersionChooserChange(event.getValue());
  }

  @UiHandler("sharePanelFrame")
  public void onSharePanelClose(CloseEvent<FramePanel> event) {
    // TODO(tpondich): The widget should know if it's state changed.
    presenter.onShareChooserClose(shareChooser.getFileVisibility());
  }

  @Override
  public void clearShareChooser() {
    shareChooser.clear();
  }

  @Override
  public void setShareChooserFileVisibility(FileVisibility fileVisibility, boolean isSimulate) {
    // TODO(tpondich): Messy. Can't guarantee that the fileVisibility is for this file.
    shareChooser.setFileVisiblity(fileVisibility, isSimulate, getCurrentCircuit().getFileKey(),
        getCurrentCircuit().getVersionKey());
  }

  @UiHandler("shareChooser")
  public void onShareChooserPanelChange(ValueChangeEvent<FileVisibility> event) {
    presenter.onShareChooserChange(event.getValue());
  }

  @Override
  public void clearPrintSettingsList() {
    printSettings.clear();
    printButtonMore.getUpFace().setText("");
  }

  @Override
  public void addToPrintSettingsList(String label, DownloadFormat format) {
    printSettings.add(label, format);
    printButtonMore.getUpFace().setText(printSettings.getSelectedItem().getExtension());
  }

  @Override
  public void hideAll() {
    hidePanels();
    hideDialogs();
    hideRightDropdowns();
  }

  public void hidePanels() {
    setParentLayerVisible(devicePanel, false);
    setParentLayerVisible(readOnlyLabel, true);
    setParentLayerVisible(crackLabel, false);
    setParentLayerVisible(singleSublistDisplay, false);
    setParentLayerVisible(graphExplorePanel, false);
  }

  @Override
  public void hideDialogs() {
    setParentLayerVisible(dialogBackPane, false);
    setParentLayerVisible(fileChooserPanelFrame, false);
    setParentLayerVisible(sharePanelFrame, false);
    setParentLayerVisible(notificationPanelFrame, false);
    setParentLayerVisible(versionChooserPanelFrame, false);
  }

  @Override
  public void hideRightDropdowns() {
    setParentLayerVisible(printSettings, false);
    printButtonMore.setDown(false);
    setParentLayerVisible(simulationSettingsPanel, false);
    simulateButtonMore.setDown(false);
  }

  @Override
  public void showDialogBackPane() {
    setParentLayerVisible(dialogBackPane, true);
  }

  @Override
  public void showShareChooserPanel() {
    hideDialogs();
    setParentLayerVisible(dialogBackPane, true);
    setParentLayerVisible(sharePanelFrame, true);
  }

  @Override
  public void showFileChooserPanel() {
    hideDialogs();
    setParentLayerVisible(dialogBackPane, true);
    setParentLayerVisible(fileChooserPanelFrame, true);
  }

  @Override
  public void showVersionChooserPanel() {
    hideDialogs();
    setParentLayerVisible(dialogBackPane, true);
    setParentLayerVisible(versionChooserPanelFrame, true);
  }

  @Override
  public void showDevicePanel() {
    hidePanels();
    setParentLayerVisible(readOnlyLabel, false);
    setParentLayerVisible(devicePanel, true);
    setParentLayerVisible(crackLabel, true);
    setParentLayerVisible(singleSublistDisplay, true);
  }

  @Override
  public void showGraphPanel() {
    hidePanels();
    setParentLayerVisible(graphExplorePanel, true);
    setParentLayerVisible(crackLabel, true);
    // Workaround for chrome 19 needing display:none to hide frames
    // as opposed to visibility:hidden.
    // This means that we can't size things in setData and must do it
    // only after it is visible.
    graphExplorePanel.onResize();
  }

  @Override
  public void setPrintButtonEnabled(boolean enabled) {
    printButton.setEnabled(enabled);
    printButtonMore.setEnabled(enabled);
  }

  @Override
  public void setNewButtonEnabled(boolean b) {
    newButton.setEnabled(b);
  }

  @Override
  public void setDeleteButtonEnabled(boolean b) {
    deleteButton.setEnabled(b);
  }

  @Override
  public void setOpenButtonEnabled(boolean b) {
    openButton.setEnabled(b);
  }

  @Override
  public void setSaveButtonEnabled(boolean b) {
    saveButton.setEnabled(b);
  }

  @Override
  public void setCopyButtonEnabled(boolean b) {
    copyButton.setEnabled(b);
  }

  @Override
  public void setVersionsButtonEnabled(boolean b) {
    versionsButton.setEnabled(b);
  }

  @Override
  public void setUndoButtonEnabled(boolean b) {
    undoButton.setEnabled(b);
  }

  @Override
  public void setRedoButtonEnabled(boolean b) {
    redoButton.setEnabled(b);
  }

  @Override
  public void setShareButtonEnabled(boolean b) {
    shareButton.setEnabled(b);
  }

  @Override
  public void setSchematicPanelEditable(boolean b) {
    schematicPanel.setEditable(b);
    nameTextBox.setStyleDependentName("uneditable", !b);
  }

  @Override
  public void setCurrentData(DataTable data) {
    graphExplorePanel.setData(data);
  }

  @Override
  public void setSimulationErrors(String errors) {
    simulationErrorsLabel.setHTML("<b>Simulation Errors</b><br />"
        + SafeHtmlUtils.htmlEscape(
            "The following errors occured while simulating. Please correct them and try again.\n\n"
                + errors).replaceAll("\n", "<br />"));
    setParentLayerVisible(simulationErrorsLabel, true);
  }

  @Override
  public void clearSimulationErrors() {
    simulationErrorsLabel.setText("You shouldn't see me.");
    setParentLayerVisible(simulationErrorsLabel, false);
  }

  @UiHandler("simulateButtonMore")
  public void onSimulationButtonMoreClick(ClickEvent event) {
    // hideRightDropdows resets the down states.
    if (simulateButtonMore.isDown()) {
      hideRightDropdowns();
      simulateButtonMore.setDown(true);
      setParentLayerVisible(simulationSettingsPanel, true);
    } else {
      hideRightDropdowns();
      setParentLayerVisible(simulationSettingsPanel, false);
    }
  }

  @UiHandler("simulationSettingsPanel")
  public void onSimulationSettingsChange(ChangeEvent event) {
    schematicPanel.getCircuit().setSimulationSettings(simulationSettingsPanel.getValues());
    schematicPanel.onCircuitChange();
    setControlsFromCurrentCircuit();
  }

  /*
   * @UiHandler("simulationSettings") public void onSimulationSettingsBlur(BlurEvent event) {
   * simulateButtonMore.setDown(false); setParentLayerVisible(simulationSettings, false); }
   */

  @UiHandler("printButtonMore")
  public void onPrintMoreClick(ClickEvent event) {
    // hideRightDropdows resets the down states.
    if (printButtonMore.isDown()) {
      hideRightDropdowns();
      printButtonMore.setDown(true);
      setParentLayerVisible(printSettings, true);
    } else {
      hideRightDropdowns();
      setParentLayerVisible(printSettings, false);
    }
  }

  @UiHandler("printSettings")
  public void onPrintSettingsChange(ChangeEvent event) {
    printButtonMore.getUpFace().setText(printSettings.getSelectedItem().getExtension());
  }

  /*
   * @UiHandler("printSettings") public void onPrintSettingsBlur(BlurEvent event) {
   * printButtonMore.setDown(false); setParentLayerVisible(printSettings, false); }
   */

  // TODO(tpondich): These should not be a part of view.

  @Override
  public void goSchematicPanelDeviceType(DeviceType deviceType) {
    devicePanel.setDeviceType(deviceType);
    schematicPanel.setDeviceType(deviceType);
  }

  @Override
  public void goSchematicPanelUndo() {
    schematicPanel.undo();
  }

  @Override
  public void goSchematicPanelRedo() {
    schematicPanel.redo();
  }

  @Override
  public void goSchematicPanelZoomIn() {
    schematicPanel.zoomIn();
  }

  @Override
  public void goSchematicPanelZoomOut() {
    schematicPanel.zoomOut();
  }

  @Override
  public void goSchematicPanelZoomFit(boolean chartsShowing) {
    // TODO(tpondich): Clean this up.
    if (chartsShowing) {
      schematicPanel.zoomFit(getOffsetWidth() / 2 + 100, 100, 100, 100);
    } else {
      schematicPanel.zoomFit(300, 100, 100, 100);
    }
  }

}
