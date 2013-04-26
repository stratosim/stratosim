package com.stratosim.client.ui.presenter.main.states;

import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.history.HistoryState;
import com.stratosim.client.history.HistoryStateFactory;
import com.stratosim.client.ui.presenter.main.MainPresenter;
import com.stratosim.client.ui.presenter.main.State;
import com.stratosim.shared.circuitmodel.Circuit;

public class SchematicNewState extends AbstractSchematicState {
  private boolean doCreate;

  public SchematicNewState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager) {
    super(display, asyncManager);
    
    doCreate = false;
  }
  
  // Constructor only for open failing on load.
  public SchematicNewState(MainPresenter.Display display, MainPresenter.AsyncManager asyncManager, boolean doCreate) {
    super(display, asyncManager);
    
    this.doCreate = doCreate;
  }
  
  @Override
  protected State getAfterSaveState() {
    return new SchematicFileState(display, asyncManager);
  }

  @Override
  public void enter() {
    // Used specially for open failing on load.
    if (doCreate) {
      Circuit defaultCircuit = StratoSimStatic.getLocalFileManager().create("Untitled");
      display.setCurrentCircuit(defaultCircuit);
      doCreate = false;
    }
  }
  
  @Override
  public HistoryState getURLState() {
    return HistoryStateFactory.newFile();
  }
}
