package com.stratosim.client.ui.presenter.main.states;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.devicemodel.DeviceType;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileRole;
import com.stratosim.shared.validator.CircuitValidator;
import com.stratosim.shared.validator.NotBlankValidator;
import com.stratosim.shared.validator.PreSimulateValidator;

public abstract class AbstractSchematicState extends AbstractPanelSettingState {

  AbstractSchematicState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager) {
    super(display, asyncManager);
  }

  @Override
  public boolean isNewButtonEnabled() {
    return true;
  }

  @Override
  public boolean isOpenButtonEnabled() {
    return true;
  }

  @Override
  public boolean isSaveButtonEnabled() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER
        || display.getCurrentCircuit().getFileRole() == FileRole.WRITER;
  }

  @Override
  public boolean isShareButtonEnabled() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER;
  }

  @Override
  public boolean isUndoButtonEnabled() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER
        || display.getCurrentCircuit().getFileRole() == FileRole.WRITER;
  }

  @Override
  public boolean isRedoButtonEnabled() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER
        || display.getCurrentCircuit().getFileRole() == FileRole.WRITER;
  }

  @Override
  public boolean isPrintButtonEnabled() {
    return true;
  }

  @Override
  public boolean isSimulateButtonEnabled() {
    return true;
  }

  @Override
  public boolean isSchematicPanelEditable() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER
        || display.getCurrentCircuit().getFileRole() == FileRole.WRITER;
  }

  @Override
  public boolean isDevicePanelVisible() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER
        || display.getCurrentCircuit().getFileRole() == FileRole.WRITER;
  }

  abstract protected State getAfterSaveState();

  @Override
  // The last used setting should be part of the circuit persistent state.
  public ImmutableList<DownloadFormat> getPrintSettingsList() {
    return ImmutableList.of(DownloadFormat.PDF, DownloadFormat.PNG, DownloadFormat.SVG,
        DownloadFormat.PS);
  }

  /**
   * Saves the circuit if it's not already saved. Optionally displays an already saved message if
   * the circuit is already saved. If the circuit is blank, displays a message and does not advance
   * the state.
   * 
   * @param next
   * @param showAlreadySavedMessage
   * @return
   */
  private State saveIfNeeded(State next, boolean showAlreadySavedMessage, boolean canBeBlank) {
    CircuitValidator validator = new NotBlankValidator();
    boolean isBlank = !validator.isValid(display.getCurrentCircuit());

    if (!canBeBlank && isBlank) {
      display.showErrorMessage(validator.getMessage());
      return this;
    }

    if (!StratoSimStatic.getLocalFileManager().isSaved(display.getCurrentCircuit()) && !isBlank) {
      return new WaitForSaveState(display, asyncManager, this, next);

    } else {
      if (showAlreadySavedMessage) {
        display.showDoneMessage("Already Saved!");
      }
      return next;

    }
  }

  @Override
  public State goSaveButtonClicked() {
    return saveIfNeeded(getAfterSaveState(), true, false);
  }

  @Override
  public State goCopyButtonClicked() {
    return new WaitForCopyState(display, asyncManager, this, new SchematicFileState(display,
        asyncManager));
  }

  @Override
  public State goDeleteButtonClicked() {
    return new DeleteConfirmationState(display, asyncManager, this);
  }

  @Override
  public State goOpenButtonClicked() {
    return saveIfNeeded(new WaitForListLatestState(display, asyncManager, getAfterSaveState()),
        false, true);
  }

  @Override
  public State goVersionsButtonClicked() {
    return saveIfNeeded(new WaitForListVersionsState(display, asyncManager, getAfterSaveState()),
        false, false);
  }

  @Override
  public State goNewButtonClicked() {
    // TODO(tpondich): Create constant name in LocalFileManager.
    Circuit defaultCircuit = StratoSimStatic.getLocalFileManager().create("Untitled");
    display.setCurrentCircuit(defaultCircuit);

    display.clearSimulationErrors();
    return saveIfNeeded(new SchematicNewState(display, asyncManager), false, true);
  }

  @Override
  public State goShareButtonClicked() {
    return saveIfNeeded(new WaitForGetFileVisibilityState(display, asyncManager,
        getAfterSaveState(), false), false, false);
  }

  @Override
  public State goPrintButtonClicked(DownloadFormat format) {
    return saveIfNeeded(new WaitForTriggerDownloadState(display, asyncManager, format,
        getAfterSaveState()), false, false);
  }

  @Override
  public State goSimulateButtonClicked() {
    Circuit circuit = display.getCurrentCircuit();

    CircuitValidator validator = new PreSimulateValidator();

    if (!validator.isValid(circuit)) {
      display.setSimulationErrors(validator.getMessage());
      display.showErrorMessage("Simulation Errors!");
      return this;
    }

    return saveIfNeeded(
        new WaitForTriggerSimulateState(display, asyncManager, getAfterSaveState()), false, false);
  }

  @Override
  public State goDevicePanelChange(DeviceType selected) {
    display.goSchematicPanelDeviceType(selected);
    return this;
  }

  @Override
  public State goUndoButtonClicked() {
    display.goSchematicPanelUndo();
    display.setControlsFromCurrentCircuit();
    return this;
  }

  @Override
  public State goRedoButtonClicked() {
    display.goSchematicPanelRedo();
    display.setControlsFromCurrentCircuit();
    return this;
  }

  @Override
  public State goZoomInButtonClicked() {
    display.goSchematicPanelZoomIn();
    return this;
  }

  @Override
  public State goZoomOutButtonClicked() {
    display.goSchematicPanelZoomOut();
    return this;
  }

  @Override
  public State goZoomFitButtonClicked() {
    display.goSchematicPanelZoomFit(false);
    return this;
  }

  @Override
  public State goWindowClose(ClosingEvent event) {
    if (!StratoSimStatic.getLocalFileManager().isSaved(display.getCurrentCircuit())
        && new NotBlankValidator().isValid(display.getCurrentCircuit())) {
      event
          .setMessage("Your circuit contains unsaved data. Are you sure you don't want to save before leaving the page?");
    }

    return this;
  }
}
