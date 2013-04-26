package com.stratosim.client.ui.presenter.main.states;

import com.google.common.collect.ImmutableList;
import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.circuitmodel.Circuit;
import com.stratosim.shared.filemodel.DownloadFormat;
import com.stratosim.shared.filemodel.FileRole;

public abstract class AbstractChartState extends AbstractPanelSettingState {

  AbstractChartState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager) {
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
  public boolean isDeleteButtonEnabled() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER;
  }

  @Override
  public boolean isSimulateButtonEnabled() {
    return true;
  }

  @Override
  public boolean isSimulateButtonDown() {
    return true;
  }

  @Override
  public boolean isPrintButtonEnabled() {
    return true;
  }

  @Override
  public boolean isSchematicPanelEditable() {
    return false;
  }

  @Override
  public boolean isGraphPanelVisible() {
    return true;
  }

  @Override
  public boolean isShareButtonEnabled() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER;
  }
  
  abstract protected State getSchematicState();

  @Override
  public ImmutableList<DownloadFormat> getPrintSettingsList() {
    return ImmutableList.of(DownloadFormat.SIMULATIONPDF, DownloadFormat.SIMULATIONPNG,
        DownloadFormat.SPICE, DownloadFormat.CSV);
  }

  @Override
  public State goDeleteButtonClicked() {
    return new DeleteConfirmationState(display, asyncManager, this);
  }

  @Override
  public State goOpenButtonClicked() {
    return new WaitForListLatestState(display, asyncManager, this);
  }

  @Override
  public State goNewButtonClicked() {
    // TODO(tpondich): Create constant name in LocalFileManager.
    // TODO(tpondich): Flagging repeated code.
    Circuit defaultCircuit = StratoSimStatic.getLocalFileManager().create("Untitled");
    display.setCurrentCircuit(defaultCircuit);

    return new SchematicNewState(display, asyncManager);
  }
  
  @Override
  public State goSimulateButtonClicked() {
    return getSchematicState();
  }

  @Override
  public State goPrintButtonClicked(DownloadFormat format) {
    return new WaitForTriggerDownloadState(display, asyncManager, format, this);
  }
  
  @Override
  public State goShareButtonClicked() {
    return new WaitForGetFileVisibilityState(display, asyncManager, this, true);
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
    display.goSchematicPanelZoomFit(true);
    return this;
  }
}
