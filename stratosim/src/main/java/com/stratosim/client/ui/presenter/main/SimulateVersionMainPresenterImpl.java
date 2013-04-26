package com.stratosim.client.ui.presenter.main;

import com.stratosim.client.ui.presenter.main.states.SchematicNewState;
import com.stratosim.client.ui.presenter.main.states.WaitForOpenVersionState;
import com.stratosim.client.ui.presenter.main.states.WaitForTriggerSimulateState;
import com.stratosim.shared.filemodel.VersionMetadataKey;

public class SimulateVersionMainPresenterImpl extends AbstractMainPresenter {

  public SimulateVersionMainPresenterImpl(Display display, AsyncManager asyncManager,
      VersionMetadataKey key) {
    super(display, asyncManager);

    setCurrentState(new WaitForOpenVersionState(display, asyncManager, new SchematicNewState(
        display, asyncManager, true), new WaitForTriggerSimulateState(display, asyncManager,
        new SchematicNewState(display, asyncManager, true)), key));
  }
}
