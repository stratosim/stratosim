package com.stratosim.client.ui.presenter.main.states;

import com.stratosim.client.history.HistoryState;
import com.stratosim.client.history.HistoryStateFactory;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.filemodel.FileRole;

public class SchematicVersionState extends AbstractSchematicState {
  public SchematicVersionState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager) {
    super(display, asyncManager);
  }
  
  @Override
  protected State getAfterSaveState() {
    return this;
  }  
  
  @Override
  public HistoryState getURLState() {
    return HistoryStateFactory.openVersion(display.getCurrentCircuit().getVersionKey());
  }
  
  @Override
  public boolean isCopyButtonEnabled() {
    return true;
  }
  
  @Override
  public boolean isDeleteButtonEnabled() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER;
  }
  
  @Override
  public boolean isVersionsButtonEnabled() {
    return display.getCurrentCircuit().getFileRole() == FileRole.OWNER
        || display.getCurrentCircuit().getFileRole() == FileRole.WRITER;
  }
  
}
