package com.stratosim.client.ui.presenter.main.states;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.visualization.client.DataTable;
import com.stratosim.client.history.HistoryState;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.client.ui.widget.ChartPanel;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.EmailAddress;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public abstract class AbstractState implements State {
  protected final MainPresenter.Display display;
  protected final MainPresenter.AsyncManager asyncManager;

  protected AbstractState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager) {
    this.display = checkNotNull(display);
    this.asyncManager = checkNotNull(asyncManager);
  }

  private State fireUnimplemented() {
    throw new UnsupportedOperationException(
        "The UI should not allow this to happen in this state: " + this.getClass().toString());
  }

  @Override
  public void enter() {

  }

  @Override
  public State goSaveButtonClicked() {
    return fireUnimplemented();
  }
  
  @Override
  public State goCopyButtonClicked() {
    return fireUnimplemented();
  }

  @Override
  public State goOpenButtonClicked() {
    return fireUnimplemented();
  }

  @Override
  public State goNewButtonClicked() {
    return fireUnimplemented();
  }
  
  @Override
  public State goDeleteButtonClicked() {
    return fireUnimplemented();
  }

  @Override
  public State goVersionsButtonClicked() {
    return fireUnimplemented();
  }
  
  @Override
  public State goVersionChooserClose() {
    return fireUnimplemented();
  }

  @Override
  public State goShareButtonClicked() {
    return fireUnimplemented();
  }

  @Override
  public State goUndoButtonClicked() {
    return fireUnimplemented();
  }

  @Override
  public State goRedoButtonClicked() {
    return fireUnimplemented();
  }


  @Override
  public State goZoomInButtonClicked() {
    return fireUnimplemented();
  }

  @Override
  public State goZoomOutButtonClicked() {
    return fireUnimplemented();
  }

  @Override
  public State goZoomFitButtonClicked() {
    return fireUnimplemented();
  }    
  
  @Override
  public State goSimulateButtonClicked() {
    return fireUnimplemented();
  }

  @Override
  public State goPrintButtonClicked(DownloadFormat format) {
    return fireUnimplemented();
  }

  @Override
  public State goFileChooserChange(FileKey fileKey) {
    return fireUnimplemented();
  }
  
  @Override
  public State goVersionChooserChange(VersionMetadataKey versionKey) {
    return fireUnimplemented();
  }


  @Override
  public State goFileChooserClose() {
    return fireUnimplemented();
  }

  @Override
  public State goChartListChange(List<Integer> selected) {
    return fireUnimplemented();
  }

  @Override
  public State goChartFrameClose() {
    return fireUnimplemented();
  }

  @Override
  public State goShareChooserClose(FileVisibility fileVisibility) {
    return fireUnimplemented();
  }

  @Override
  public State goShareChooserChange(EmailAddress emailAddress, FileRole fileRole) {
    return fireUnimplemented();
  }
  
  @Override
  public State goAlertDialogChange() {
    return fireUnimplemented();
  }

  @Override
  public State goConfirmationDialogChange(boolean isYes) {
    return fireUnimplemented();
  }

  @Override
  public State goSaveCallbackSuccess(VersionMetadata metadata) {
    return fireUnimplemented();
  }

  @Override
  public State goSaveCallbackFailure() {
    return fireUnimplemented();
  }
  
  @Override
  public State goDeleteCallbackSuccess() {
    return fireUnimplemented();
  }

  @Override
  public State goDeleteCallbackFailure() {
    return fireUnimplemented();
  }
  
  @Override
  public State goDeleteCallbackDeletingWithCollaboratorsException() {
    return fireUnimplemented();
  }
  
  @Override
  public State goCopyCallbackSuccess(VersionMetadata metadata) {
    return fireUnimplemented();
  }

  @Override
  public State goCopyCallbackFailure() {
    return fireUnimplemented();
  }

  @Override
  public State goOpenCallbackSuccess(Circuit result) {
    return fireUnimplemented();
  }

  @Override
  public State goOpenCallbackFailure() {
    return fireUnimplemented();
  }
  
  @Override
  public State goOpenCallbackAccessException() {
    return fireUnimplemented();
  }
  
  @Override
  public State goOpenCallbackPersistenceException() {
    return fireUnimplemented();
  }

  @Override
  public State goSimulationDatatableCallbackSuccess(DataTable data) {
    return fireUnimplemented();
  }

  @Override
  public State goSimulationDatatableCallbackFailure() {
    return fireUnimplemented();
  }

  @Override
  public State goCircuitDownloadAvailableCallbackSuccess(boolean isAvailable) {
    return fireUnimplemented();
  }

  @Override
  public State goCircuitDownloadAvailableCallbackFailure() {
    return fireUnimplemented();
  }
  
  @Override
  public State goGetPermissionsCallbackSuccess(FileVisibility result) {
    return fireUnimplemented();
  }

  @Override
  public State goGetPermissionsCallbackFailure() {
    return fireUnimplemented();
  }

  @Override
  public State goSetPermissionsCallbackSuccess() {
    return fireUnimplemented();
  }

  @Override
  public State goSetPermissionsCallbackFailure() {
    return fireUnimplemented();
  }
  
  @Override
  public State goListLatestCallbackFailure() {
    return fireUnimplemented();
  }

  @Override
  public State goListLatestCallbackSuccess(ImmutableList<VersionMetadata> result) {
    return fireUnimplemented();
  }
  
  @Override
  public State goListVersionsCallbackFailure() {
    return fireUnimplemented();
  }
  
  @Override
  public State goListVersionsCallbackSuccess(ImmutableList<VersionMetadata> fileList) {
    return fireUnimplemented();
  }
  
  @Override
  public State goRetryTimerCallback() {
    return fireUnimplemented();
  }
  
  @Override
  public State goWindowClose(ClosingEvent event) {
    return this;
  }

  @Override
  public State goDevicePanelChange(DeviceType type) {
    return fireUnimplemented();
  }
  
  @Override
  public State goShareChooserChange(FileVisibility items) {
    return fireUnimplemented();
  }
  
  @Override
  public State goImageLoadCallbackFailure() {
    return fireUnimplemented();
  }

  @Override
  public State goImageLoadCallbackSuccess(String imageUrl) {
    return fireUnimplemented();
  }
  
  @Override
  public State goTriggerGenerateCallbackFailure() {
    return fireUnimplemented();
  }
  
  @Override
  public State goTriggerGenerateCallbackSuccess(boolean isReady) {
    return fireUnimplemented();
  }
  
  @Override
  public State goGetLatestThumbnailsCallbackSuccess(ImmutableMap<FileKey, String> data) {
    return fireUnimplemented();
  }

  @Override
  public State goGetLatestThumbnailsCallbackFailure() {
    return fireUnimplemented();
  }
  
  @Override
  public State goTimerCallback() {
    return fireUnimplemented();
  }

  @Override
  public boolean isNewButtonEnabled() {
    return false;
  }
  
  @Override
  public boolean isDeleteButtonEnabled() {
    return false;
  }

  @Override
  public boolean isOpenButtonEnabled() {
    return false;
  }

  @Override
  public boolean isSaveButtonEnabled() {
    return false;
  }
  
  @Override
  public boolean isCopyButtonEnabled() {
    return false;
  }

  @Override
  public boolean isShareButtonEnabled() {
    return false;
  }

  @Override
  public boolean isVersionsButtonEnabled() {
    return false;
  }
  
  @Override
  public boolean isUndoButtonEnabled() {
    return false;
  }

  @Override
  public boolean isRedoButtonEnabled() {
    return false;
  }

  @Override
  public boolean isPrintButtonEnabled() {
    return false;
  }

  @Override
  public boolean isSimulateButtonEnabled() {
    return false;
  }
  
  @Override
  public boolean isSimulateButtonDown() {
    return false;
  }

  @Override
  public boolean isSchematicPanelEditable() {
    return false;
  }

  @Override
  public boolean isFileChooserVisible() {
    return false;
  }

  @Override
  public boolean isVersionChooserVisible() {
    return false;
  } 
 
  @Override
  public boolean isDevicePanelVisible() {
    return false;
  }

  @Override
  public boolean isGraphPanelVisible() {
    return false;
  }
  
  @Override
  public boolean isShareChooserVisible() {
    return false;
  }
  
  @Override
  public boolean isDialogBackPaneVisible() {
    return false;
  }
  
  public boolean isNotificationPanelVisible() {
    return false;
  }

  public String getNotificationMessage() {
    return null;
  }

  public String getNotificationCaption() {
    return null;
  }

  @Override
  public @Nullable ImmutableSortedMap<VersionMetadata, Optional<Image>> getFileChooserList() {
    return null;
  }
  
  @Override
  public @Nullable ImmutableList<VersionMetadata> getVersionChooserList() {
    return null;
  }

  @Override
  public @Nullable ImmutableList<DownloadFormat> getPrintSettingsList() {
    return null;
  }

  @Override
  public @Nullable ImmutableList<ChartPanel> getChartList() {
    return null;
  }

  @Override
  public @Nullable FileVisibility getShareChooserFileVisibility() {
    return null;
  }
  
  @Override
  public boolean getShareChooserIsSimulate() {
    return false;
  }
  
  @Override
  public boolean doHidePanels() {
    return false;
  }

  public @Nullable HistoryState getURLState() {
    return null;
  }

}
