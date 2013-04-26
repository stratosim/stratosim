package com.stratosim.client.ui.presenter.main;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.visualization.client.DataTable;
import com.stratosim.client.ui.presenter.Presenter;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileKey;
import com.stratosim.shared.filemodel.FileVisibility;
import com.stratosim.shared.filemodel.VersionMetadata;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public interface MainPresenter extends Presenter {

  // States access Display to alter the UI state.
  interface Display extends com.stratosim.client.ui.presenter.Display<MainPresenter> {
    void setPresenter(MainPresenter presenter);

    // Left Panels
    void hideAll();
    void showDevicePanel();
    void showGraphPanel();
    
    // Dialogs
    void showShareChooserPanel();
    void showFileChooserPanel();
    void showVersionChooserPanel();
    
    // Right Panels
    void hideRightDropdowns();
    // TODO(tpondich): The other wiring is in MainView.
    
    // SchematicPanel
    Circuit getCurrentCircuit();
    void setCurrentCircuit(Circuit circuit);
    void setSchematicPanelEditable(boolean editable);

    // TODO(tpondich): Out of place?
    void goSchematicPanelDeviceType(DeviceType type);
    void goSchematicPanelUndo();
    void goSchematicPanelRedo();
    
    void goSchematicPanelZoomIn();
    void goSchematicPanelZoomOut();
    void goSchematicPanelZoomFit(boolean chartsShowing);
    
    // TODO(tpondich): Out of place?
    void setControlsFromCurrentCircuit();

    // Print Settings
    void clearPrintSettingsList();
    void addToPrintSettingsList(String label, DownloadFormat format);

    // Share Settings
    void clearShareChooser();
    void setShareChooserFileVisibility(FileVisibility fileVisibility, boolean isSimulate);
    
    // File List
    void clearFileList();
    void addToFileList(VersionMetadata versionMetadata, Image thumbnail);
    
    // Version List
    void clearVersionList();
    void addToVersionList(VersionMetadata versionMetadata);

    // GraphPanel and GraphExplorePanel
    void setCurrentData(DataTable data);

    // Simulation Errors
    void setSimulationErrors(String errors);
    void clearSimulationErrors();

    // Buttons
    void setSimulateButtonEnabled(boolean enabled);
    void setSimulateButtonDown(boolean down);
    void setPrintButtonEnabled(boolean enabled);
    void setNewButtonEnabled(boolean enabled);
    void setOpenButtonEnabled(boolean enabled);
    void setSaveButtonEnabled(boolean enabled);
    void setShareButtonEnabled(boolean enabled);
    void setUndoButtonEnabled(boolean b);
    void setRedoButtonEnabled(boolean b);
    void setCopyButtonEnabled(boolean b);
    void setDeleteButtonEnabled(boolean b);
    void setVersionsButtonEnabled(boolean b);

    // Messages
    void showWorkingMessage(String message);
    void showDoneMessage(String message);
    void showErrorMessage(String message);
    void clearMessage();

    void showAlert(String caption, String message);
    void showConfirmation(String caption, String message);

    void hideDialogs();
    void hidePanels();

    void showDialogBackPane();
  }
  
  // States call AsyncManager to fire Asyncs (GWT RPC, cached, or otherwise)
  interface AsyncManager {
    void setPresenter(MainPresenter presenter);

    void fireSaveAsync(Circuit circuit);
    void fireCopyAsync(Circuit circuit);
    void fireOpenAsync(FileKey key);
    void fireOpenAsync(VersionMetadataKey key);
    void fireListLatestAsync();
    void fireListVersionsAsync(FileKey fileKey);
    void fireGetFileVisibilityAsync(FileKey fileKey);
    void fireSetFileVisibilityAsync(FileKey fileKey, FileVisibility fileVisibility);
    void fireDownloadAvailableAsync(VersionMetadataKey versionKey, DownloadFormat format);
    void fireTriggerRenderAsync(VersionMetadataKey versionKey, DownloadFormat format);
    void fireSimulationDatatableAsync(VersionMetadataKey versionKey);
    void fireDeleteAsync(FileKey key);
    void fireGetLatestThumbnailsAsync();
    void fireTimerAsync(int timeout);

  }
  
  // Presenter receives these events and then calls into the state machine
  // to change state.

  // Button Events
  void onSaveButtonClicked();
  void onOpenButtonClicked();
  void onNewButtonClicked();
  void onShareButtonClicked();
  void onPrintButtonClicked(DownloadFormat downloadFormat);
  void onSimulateButtonClicked();
  void onUndoButtonClicked();
  void onRedoButtonClicked();
  void onCopyButtonClicked();
  void onDeleteButtonClicked();
  void onVersionsButtonClicked();
  void onZoomInButtonClicked();
  void onZoomOutButtonClicked();
  void onZoomFitButtonClicked();
  
  // Dialog Closing
  void onFileChooserClose();
  void onVersionChooserClose();
  void onShareChooserClose(FileVisibility fileVisibility);
  
  // Dialog Commits
  void onDevicePanelChange(DeviceType selected);
  void onChartChooserChange(List<Integer> list);
  void onFileChooserChange(FileKey fileKey);
  void onShareChooserChange(FileVisibility items);
  void onVersionChooserChange(VersionMetadataKey versionKey);
  
  void onAlertDialogChange();
  void onConfirmationDialogChange(boolean b);

  // Window Closing
  void onWindowClose(ClosingEvent event);
  
  // Async Callbacks
  void goSaveCallbackSuccess(VersionMetadata result);
  void goSaveCallbackFailure();
  void goCopyCallbackSuccess(VersionMetadata result);
  void goCopyCallbackFailure();
  void goOpenCallbackSuccess(Circuit result);
  void goOpenCallbackFailure();
  void goOpenCallbackAccessException();
  void goOpenCallbackPersistenceException();
  void goSimulationDatatableCallbackSuccess(DataTable data);
  void goSimulationDatatableCallbackFailure();
  void goCircuitDownloadAvailableCallbackSuccess(boolean isAvailable);
  void goCircuitDownloadAvailableCallbackFailure();
  void goRetryTimerCallback();
  void goListLatestCallbackFailure();
  void goListLatestCallbackSuccess(ImmutableList<VersionMetadata> result);
  void goListVersionsCallbackFailure();
  void goListVersionsCallbackSuccess(ImmutableList<VersionMetadata> result);
  void goGetPermissionsCallbackFailure();
  void goGetPermissionsCallbackSuccess(FileVisibility result);
  void goImageLoadCallbackFailure();
  void goImageLoadCallbackSuccess(String imageUrl);
  void goTriggerCallbackFailure();
  void goTriggerCallbackSuccess(boolean v);
  void goSetPermissionsCallbackSuccess();
  void goSetPermissionsCallbackFailure();
  void goDeleteCallbackFailure();
  void goDeleteCallbackDeletingWithCollaboratorsException();
  void goDeleteCallbackSuccess();
  void goGetLatestThumbnailsCallbackSuccess(ImmutableMap<FileKey, String> data);
  void goGetLatestThumbnailsCallbackFailure();
  void goTimerCallback();

}
