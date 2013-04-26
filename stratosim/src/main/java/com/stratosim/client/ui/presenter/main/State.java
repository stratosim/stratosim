package com.stratosim.client.ui.presenter.main;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.visualization.client.DataTable;
import com.stratosim.client.history.HistoryState;
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

public interface State {
  // UI State
  boolean isNewButtonEnabled();
  boolean isDeleteButtonEnabled();
  boolean isOpenButtonEnabled();
  boolean isSaveButtonEnabled();
  boolean isShareButtonEnabled();
  boolean isUndoButtonEnabled();
  boolean isRedoButtonEnabled();
  boolean isPrintButtonEnabled();
  boolean isSimulateButtonEnabled();
  boolean isSchematicPanelEditable();
  boolean isFileChooserVisible();
  boolean isVersionChooserVisible();
  boolean isDevicePanelVisible();
  boolean isGraphPanelVisible();
  boolean isSimulateButtonDown();
  boolean isShareChooserVisible();
  boolean isCopyButtonEnabled();
  boolean isVersionsButtonEnabled();
  
  boolean isDialogBackPaneVisible();
  
  boolean doHidePanels();
  
  HistoryState getURLState();
  
  ImmutableSortedMap<VersionMetadata, Optional<Image>> getFileChooserList();
  ImmutableList<VersionMetadata> getVersionChooserList();
  ImmutableList<DownloadFormat> getPrintSettingsList();
  ImmutableList<ChartPanel> getChartList();
  FileVisibility getShareChooserFileVisibility();
  boolean getShareChooserIsSimulate();
  
  // Called when the state is first set in Presenter. Does not reenter
  // if next state == this.
  void enter();

  // UI Handlers
  State goSaveButtonClicked();
  State goOpenButtonClicked();
  State goNewButtonClicked();
  State goDeleteButtonClicked();
  State goVersionsButtonClicked();
  State goShareButtonClicked();
  State goPrintButtonClicked(DownloadFormat format);
  State goSimulateButtonClicked();
  State goUndoButtonClicked();
  State goRedoButtonClicked();
  State goCopyButtonClicked();
  State goZoomInButtonClicked();
  State goZoomOutButtonClicked();
  State goZoomFitButtonClicked();
  State goFileChooserChange(FileKey fileKey);
  State goFileChooserClose();
  State goVersionChooserChange(VersionMetadataKey versionKey);
  State goVersionChooserClose();
  State goChartListChange(List<Integer> selectedItems);
  State goChartFrameClose();
  State goDevicePanelChange(DeviceType selected);
  State goShareChooserClose(FileVisibility fileVisibility);
  State goShareChooserChange(EmailAddress emailAddress, FileRole fileRole);
  State goWindowClose(ClosingEvent event);
  State goShareChooserChange(FileVisibility items);
  State goAlertDialogChange();
  State goConfirmationDialogChange(boolean isYes);
  
  // RPC Handlers
  State goSaveCallbackSuccess(VersionMetadata result);
  State goSaveCallbackFailure();
  State goCopyCallbackSuccess(VersionMetadata result);
  State goCopyCallbackFailure();
  State goOpenCallbackSuccess(Circuit result);
  State goOpenCallbackFailure();
  State goOpenCallbackAccessException();
  State goOpenCallbackPersistenceException();
  State goSimulationDatatableCallbackSuccess(DataTable data);
  State goSimulationDatatableCallbackFailure();
  State goCircuitDownloadAvailableCallbackSuccess(boolean isAvailable);
  State goCircuitDownloadAvailableCallbackFailure();
  State goRetryTimerCallback();
  State goListLatestCallbackFailure();
  State goListLatestCallbackSuccess(ImmutableList<VersionMetadata> result);
  State goListVersionsCallbackFailure();
  State goListVersionsCallbackSuccess(ImmutableList<VersionMetadata> result);
  State goGetPermissionsCallbackFailure();
  State goGetPermissionsCallbackSuccess(FileVisibility result);
  State goImageLoadCallbackFailure();
  State goImageLoadCallbackSuccess(String imageUrl);
  State goTriggerGenerateCallbackFailure();
  State goTriggerGenerateCallbackSuccess(boolean v);
  State goSetPermissionsCallbackSuccess();
  State goSetPermissionsCallbackFailure();
  State goDeleteCallbackSuccess();
  State goDeleteCallbackFailure();
  State goDeleteCallbackDeletingWithCollaboratorsException();
  State goGetLatestThumbnailsCallbackSuccess(ImmutableMap<FileKey, String> data);
  State goGetLatestThumbnailsCallbackFailure();
  State goTimerCallback();

}
