package com.stratosim.client.ui.presenter.main;

import com.stratosim.client.StratoSimStatic;
import com.stratosim.client.ui.presenter.main.states.SchematicNewState;
import com.stratosim.shared.circuitmodel.Circuit;

public class NewMainPresenterImpl extends AbstractMainPresenter {

  public NewMainPresenterImpl(Display display, AsyncManager asyncManager) {
    super(display, asyncManager);
    Circuit defaultCircuit = StratoSimStatic.getLocalFileManager().create("Untitled");
    display.setCurrentCircuit(defaultCircuit);
    setCurrentState(new SchematicNewState(display, asyncManager));
  }
}
