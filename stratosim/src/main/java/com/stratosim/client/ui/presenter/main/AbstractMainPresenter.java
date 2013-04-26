package com.stratosim.client.ui.presenter.main;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.visualization.client.DataTable;
import com.stratosim.client.ui.presenter.AbstractPresenter;
import com.stratosim.client.ui.presenter.main.states.SchematicNewState;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public abstract class AbstractMainPresenter extends AbstractPresenter implements MainPresenter {

  private final MainPresenter.Display display;
  // private final MainPresenter.AsyncManager asyncManager;
  private State currentState;

  public AbstractMainPresenter(MainPresenter.Display display,
      MainPresenter.AsyncManager asyncManager) {
    this.display = checkNotNull(display);
    display.setPresenter(this);
    // this.asyncManager = checkNotNull(asyncManager);
    checkNotNull(asyncManager);
    asyncManager.setPresenter(this);

    setCurrentState(new SchematicNewState(display, asyncManager));
  }

  @Override
  public void go(HasWidgets container) {
    container.clear();
    container.add(display.asWidget());
  }

  protected void setCurrentState(State newState) {
    // If the nextState is the same as the currentState,
    // then no initialization should be necessary.

    if (currentState == newState) {
      return;
    }

    currentState = checkNotNull(newState);

    display.setNewButtonEnabled(currentState.isNewButtonEnabled());
    display.setOpenButtonEnabled(currentState.isOpenButtonEnabled());
    display.setSaveButtonEnabled(currentState.isSaveButtonEnabled());
    display.setCopyButtonEnabled(currentState.isCopyButtonEnabled());
    display.setVersionsButtonEnabled(currentState.isVersionsButtonEnabled());
    display.setDeleteButtonEnabled(currentState.isDeleteButtonEnabled());
    display.setShareButtonEnabled(currentState.isShareButtonEnabled());
    display.setUndoButtonEnabled(currentState.isUndoButtonEnabled());
    display.setRedoButtonEnabled(currentState.isRedoButtonEnabled());
    display.setPrintButtonEnabled(currentState.isPrintButtonEnabled());
    display.setSimulateButtonEnabled(currentState.isSimulateButtonEnabled());
    display.setSimulateButtonDown(currentState.isSimulateButtonDown());

    display.setSchematicPanelEditable(currentState.isSchematicPanelEditable());

    display.clearPrintSettingsList();
    if (currentState.getPrintSettingsList() != null) {
      for (DownloadFormat item : currentState.getPrintSettingsList()) {
        display.addToPrintSettingsList(item.getExtension(), item);
      }
    }

    display.hideDialogs();
    if (currentState.isFileChooserVisible()) {
      display.clearFileList();
      if (currentState.getFileChooserList() != null) {
        for (Map.Entry<VersionMetadata, Optional<Image>> item : currentState.getFileChooserList().entrySet()) {
          display.addToFileList(item.getKey(), item.getValue().orNull());
        }
      }
      display.showFileChooserPanel();
    } else if (currentState.isVersionChooserVisible()) {
      display.clearVersionList();
      if (currentState.getVersionChooserList() != null) {
        for (VersionMetadata item : currentState.getVersionChooserList()) {
          display.addToVersionList(item);
        }
      }
      display.showVersionChooserPanel();
    } else if (currentState.isShareChooserVisible()) {
      display.showShareChooserPanel();
      display.setShareChooserFileVisibility(currentState.getShareChooserFileVisibility(),
          currentState.getShareChooserIsSimulate());
    }

    if (currentState.isDialogBackPaneVisible()) {
      display.showDialogBackPane();
    }

    if (currentState.doHidePanels()) {
      display.hidePanels();
    }
    if (currentState.isDevicePanelVisible()) {
      display.showDevicePanel();
    } else if (currentState.isGraphPanelVisible()) {
      display.showGraphPanel();
    }

    if (currentState.getURLState() != null) {
      currentState.getURLState().fire();
    }

    currentState.enter();
  }

  protected State getCurrentState() {
    return currentState;
  }

  // UI Events

  @Override
  public void onSaveButtonClicked() {
    setCurrentState(getCurrentState().goSaveButtonClicked());
  }

  @Override
  public void onCopyButtonClicked() {
    setCurrentState(getCurrentState().goCopyButtonClicked());
  }

  @Override
  public void onDeleteButtonClicked() {
    setCurrentState(getCurrentState().goDeleteButtonClicked());
  }

  @Override
  public void onOpenButtonClicked() {
    setCurrentState(getCurrentState().goOpenButtonClicked());
  }

  @Override
  public void onVersionsButtonClicked() {
    setCurrentState(getCurrentState().goVersionsButtonClicked());
  }

  @Override
  public void onNewButtonClicked() {
    setCurrentState(getCurrentState().goNewButtonClicked());
  }

  @Override
  public void onShareButtonClicked() {
    setCurrentState(getCurrentState().goShareButtonClicked());
  }

  @Override
  public void onUndoButtonClicked() {
    setCurrentState(getCurrentState().goUndoButtonClicked());
  }

  @Override
  public void onRedoButtonClicked() {
    setCurrentState(getCurrentState().goRedoButtonClicked());
  }

  @Override
  public void onZoomInButtonClicked() {
    setCurrentState(getCurrentState().goZoomInButtonClicked());
  }

  @Override
  public void onZoomOutButtonClicked() {
    setCurrentState(getCurrentState().goZoomOutButtonClicked());
  }

  @Override
  public void onZoomFitButtonClicked() {
    setCurrentState(getCurrentState().goZoomFitButtonClicked());
  }

  @Override
  public void onPrintButtonClicked(DownloadFormat format) {
    setCurrentState(getCurrentState().goPrintButtonClicked(format));
  }

  @Override
  public void onSimulateButtonClicked() {
    setCurrentState(getCurrentState().goSimulateButtonClicked());
  }

  @Override
  public void onFileChooserChange(FileKey fileKey) {
    checkNotNull(fileKey);

    setCurrentState(getCurrentState().goFileChooserChange(fileKey));
  }

  @Override
  public void onVersionChooserChange(VersionMetadataKey versionKey) {
    checkNotNull(versionKey);

    setCurrentState(getCurrentState().goVersionChooserChange(versionKey));
  }


  @Override
  public void onShareChooserChange(FileVisibility items) {
    checkNotNull(items);

    setCurrentState(getCurrentState().goShareChooserChange(items));
  }

  @Override
  public void onWindowClose(ClosingEvent event) {
    setCurrentState(getCurrentState().goWindowClose(event));
  }

  @Override
  public void onShareChooserClose(FileVisibility fileVisibility) {
    setCurrentState(getCurrentState().goShareChooserClose(fileVisibility));
  }

  @Override
  public void onDevicePanelChange(DeviceType selected) {
    setCurrentState(getCurrentState().goDevicePanelChange(selected));
  }

  @Override
  public void onFileChooserClose() {
    setCurrentState(getCurrentState().goFileChooserClose());
  }

  @Override
  public void onVersionChooserClose() {
    setCurrentState(getCurrentState().goVersionChooserClose());
  }

  @Override
  public void onChartChooserChange(List<Integer> selectedItems) {
    setCurrentState(getCurrentState().goChartListChange(selectedItems));
  }

  @Override
  public void onAlertDialogChange() {
    setCurrentState(getCurrentState().goAlertDialogChange());
  }

  @Override
  public void onConfirmationDialogChange(boolean isYes) {
    setCurrentState(getCurrentState().goConfirmationDialogChange(isYes));
  }

  // Async Callbacks

  @Override
  public void goSaveCallbackSuccess(VersionMetadata result) {
    setCurrentState(currentState.goSaveCallbackSuccess(result));
  }

  @Override
  public void goSaveCallbackFailure() {
    setCurrentState(currentState.goSaveCallbackFailure());
  }

  @Override
  public void goCopyCallbackSuccess(VersionMetadata result) {
    setCurrentState(currentState.goCopyCallbackSuccess(result));
  }

  @Override
  public void goCopyCallbackFailure() {
    setCurrentState(currentState.goCopyCallbackFailure());
  }

  @Override
  public void goOpenCallbackSuccess(Circuit result) {
    setCurrentState(currentState.goOpenCallbackSuccess(result));
  }

  @Override
  public void goOpenCallbackFailure() {
    setCurrentState(currentState.goOpenCallbackFailure());
  }

  @Override
  public void goOpenCallbackAccessException() {
    setCurrentState(currentState.goOpenCallbackAccessException());
  }

  @Override
  public void goOpenCallbackPersistenceException() {
    setCurrentState(currentState.goOpenCallbackPersistenceException());
  }

  @Override
  public void goSimulationDatatableCallbackSuccess(DataTable data) {
    setCurrentState(currentState.goSimulationDatatableCallbackSuccess(data));
  }

  @Override
  public void goSimulationDatatableCallbackFailure() {
    setCurrentState(currentState.goSimulationDatatableCallbackFailure());
  }

  @Override
  public void goCircuitDownloadAvailableCallbackSuccess(boolean isAvailable) {
    setCurrentState(currentState.goCircuitDownloadAvailableCallbackSuccess(isAvailable));
  }

  @Override
  public void goCircuitDownloadAvailableCallbackFailure() {
    setCurrentState(currentState.goCircuitDownloadAvailableCallbackFailure());
  }

  @Override
  public void goRetryTimerCallback() {
    setCurrentState(currentState.goRetryTimerCallback());
  }

  @Override
  public void goListLatestCallbackFailure() {
    setCurrentState(currentState.goListLatestCallbackFailure());
  }

  @Override
  public void goListLatestCallbackSuccess(ImmutableList<VersionMetadata> result) {
    setCurrentState(currentState.goListLatestCallbackSuccess(result));
  }

  @Override
  public void goListVersionsCallbackFailure() {
    setCurrentState(currentState.goListVersionsCallbackFailure());
  }

  @Override
  public void goListVersionsCallbackSuccess(ImmutableList<VersionMetadata> result) {
    setCurrentState(currentState.goListVersionsCallbackSuccess(result));
  }

  @Override
  public void goGetPermissionsCallbackFailure() {
    setCurrentState(currentState.goGetPermissionsCallbackFailure());
  }

  @Override
  public void goGetPermissionsCallbackSuccess(FileVisibility result) {
    setCurrentState(currentState.goGetPermissionsCallbackSuccess(result));
  }

  @Override
  public void goImageLoadCallbackFailure() {
    setCurrentState(currentState.goImageLoadCallbackFailure());
  }

  @Override
  public void goImageLoadCallbackSuccess(String imageUrl) {
    setCurrentState(currentState.goImageLoadCallbackSuccess(imageUrl));
  }

  @Override
  public void goTriggerCallbackFailure() {
    setCurrentState(currentState.goTriggerGenerateCallbackFailure());
  }

  @Override
  public void goTriggerCallbackSuccess(boolean isAvailable) {
    setCurrentState(currentState.goTriggerGenerateCallbackSuccess(isAvailable));
  }

  @Override
  public void goSetPermissionsCallbackSuccess() {
    setCurrentState(currentState.goSetPermissionsCallbackSuccess());
  }

  @Override
  public void goSetPermissionsCallbackFailure() {
    setCurrentState(currentState.goSetPermissionsCallbackFailure());
  }

  @Override
  public void goDeleteCallbackSuccess() {
    setCurrentState(currentState.goDeleteCallbackSuccess());
  }

  @Override
  public void goDeleteCallbackFailure() {
    setCurrentState(currentState.goDeleteCallbackFailure());
  }

  @Override
  public void goDeleteCallbackDeletingWithCollaboratorsException() {
    setCurrentState(currentState.goDeleteCallbackDeletingWithCollaboratorsException());
  }

  @Override
  public void goGetLatestThumbnailsCallbackSuccess(ImmutableMap<FileKey, String> data) {
    setCurrentState(currentState.goGetLatestThumbnailsCallbackSuccess(data));
  }

  @Override
  public void goGetLatestThumbnailsCallbackFailure() {
    setCurrentState(currentState.goGetLatestThumbnailsCallbackFailure());
  }

  @Override
  public void goTimerCallback() {
    setCurrentState(currentState.goTimerCallback());
  }
}
